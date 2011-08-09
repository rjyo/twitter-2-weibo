/*
 * (The MIT License)
 *
 * Copyright (c) 2011 Rakuraku Jyo <jyo.rakuraku@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the 'Software'), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 */

package h2weibo;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;
import org.jets3t.service.S3Service;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.security.AWSCredentials;
import weibo4j.Weibo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * @author Rakuraku Jyo
 */
public class S3BackupTask extends DBTask {
    private static final Logger log = Logger.getLogger(S3BackupTask.class.getName());
    private String awsAccessKey;
    private String awsSecretAccessKey;

    public S3BackupTask() {
        this.awsAccessKey = System.getProperty("h2weibo.awsAccessKey");
        this.awsSecretAccessKey = System.getProperty("h2weibo.awsSecretAccessKey");
    }

    public S3BackupTask(String awsAccessKey, String awsSecretAccessKey) {
        this.awsAccessKey = awsAccessKey;
        this.awsSecretAccessKey = awsSecretAccessKey;
    }

    public void run() {
        String dump = getHelper().dump();

        try {
            AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretAccessKey);
            S3Service s3Service = new RestS3Service(awsCredentials);
            S3Bucket backup = s3Service.getBucket("h2weibo.backup");

            String dateString = DateFormatUtils.format(new Date(), "yyyy-MM-dd_HH");
            S3Object object = new S3Object(dateString + ".json", dump);
            object = s3Service.putObject(backup, object);
            System.out.println("S3Object after upload: " + object);
        } catch (Exception e) {
            log.error("Failed to upload to S3.");
            log.error(e);
        }
    }

    public void restore(String from) {
        try {
            AWSCredentials awsCredentials = new AWSCredentials(awsAccessKey, awsSecretAccessKey);
            S3Service s3Service = new RestS3Service(awsCredentials);

            S3Object object = s3Service.getObject("h2weibo.backup", from + ".json");
            InputStream inputStream = object.getDataInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            String data = "";
            while ((line = reader.readLine()) != null) {
                data += line;
            }
            reader.close();

            getHelper().restore(data);
        } catch (Exception e) {
            log.error("Failed to restore from S3.");
            log.error(e);
        }
    }
}
