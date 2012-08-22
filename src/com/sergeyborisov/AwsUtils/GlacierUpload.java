package com.sergeyborisov.AwsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import com.amazonaws.auth.*;
import com.amazonaws.services.glacier.AmazonGlacierClient;
import com.amazonaws.services.glacier.transfer.ArchiveTransferManager;
import com.amazonaws.services.glacier.transfer.UploadResult;


public class GlacierUpload {
    private String _vaultName;
    private String _region = "us-east-1";
    private List<String> _filenames;
    private AWSCredentials _credentials;

    private final static String usageString =
            "GlacierUpload (c) 2012 Sergey Borisov (risik)\n" +
                    "Based on AWS's sample for Glacier Upload:\n" +
                    "http://docs.amazonwebservices.com/amazonglacier/latest/dev/getting-started-upload-archive-java.html\n\n" +
                    "Usage:\n" +
                    "\tjava -jar GlacierUpload.jar <parameters> filenames\n" +
                    "\tparameters:\n" +
                    "\t\t--vault=vaultname\n" +
                    "\t\t--region=regionname ('us-east-1' by default)\n"
            ;

    public static void main(String[] args) throws IOException {
        try {
            GlacierUpload glacierUpload = new GlacierUpload();
            glacierUpload.run(args);
        }
        catch (Exception ex) {
            System.err.print("Error: " + ex.getMessage());
        }
    }

    private GlacierUpload() {
        _filenames = new ArrayList<String>();
    }

    public Map<String, String> process() throws FileNotFoundException {
        return process(
                _vaultName,
                _region,
                _filenames,
                _credentials
        );
    }

    public Map<String, String> process(
            String vaultName,
            String region,
            List<String> filenames,
            AWSCredentials credentials
    )
            throws FileNotFoundException
    {
        AmazonGlacierClient client = new AmazonGlacierClient(credentials);
        client.setEndpoint(genUrlForRegion(region));

        ArchiveTransferManager atm = new ArchiveTransferManager(client, credentials);

        Map <String, String> resultIds = new HashMap<String, String>();

        for (String filename: filenames) {
            UploadResult result = atm.upload(vaultName, "my archive " + (new Date()), new File(filename));
            String resultId = result.getArchiveId();
            resultIds.put(filename, resultId);
        }

        return resultIds;
    }

    private void usage() {
        System.out.println(usageString);
    }

    private void run(String[] args) throws FileNotFoundException {
        if (!parseCommandLine(args)) {
            usage();
            return;
        }

//        AWSCredentialsProvider credentialsProvider = new SystemPropertiesCredentialsProvider();
        AWSCredentialsProvider credentialsProvider = new EnvironmentVariableCredentialsProvider();
        _credentials = credentialsProvider.getCredentials();

        Map<String, String> archiveIds = process();
        for (String filename: archiveIds.keySet()) {
            String archiveId = archiveIds.get(filename);
            System.out.println("File: " + filename + " archive ID: " + archiveId);
        }
    }

    private boolean parseCommandLine(String[] args) {
        for(String arg: args) {
            parseArg(arg);
        }
        return (_vaultName != null) && (_filenames.size() > 0);
    }

    private void parseArg(String arg) {
        if (arg.startsWith("--")) {
            String []pair = parseParam(arg);
            if (pair == null)
                return;

            String paramName = pair[0];
            String paramValue = pair[1];
            if ("--vault".equals(paramName))
                _vaultName = paramValue;
            else if ("--region".equals(paramName))
                _region = paramValue;
        }
        else {
            _filenames.add(arg);
        }
    }

    private String[] parseParam(String arg) {
        Scanner scanner = new Scanner(arg).useDelimiter("\\s*=\\s*");

        String name = scanner.next();
        if ("".equals(name))
            return null;

        String value = scanner.next();
        if ("".equals(value))
            return null;

        scanner.close();

        String[] res = new String[2];
        res[0] = name;
        res[1] = value;

        return res;
    }

    private String genUrlForRegion(final String region) {
        StringBuilder sb = new StringBuilder("https://glacier.");
        sb.append(region).append(".amazonaws.com/");
        return sb.toString();
    }

}