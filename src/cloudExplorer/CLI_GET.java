/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.package
 * cloudExplorer
 *
 */
package cloudExplorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class CLI_GET {

    String name = null;
    String Home = System.getProperty("user.home");
    String s3_config_file = Home + File.separator + "s3.config";
    BucketClass Bucket = new BucketClass();
    Acl objectacl = new Acl();
    String get_file;
    Get get;
    String build_name = null;
    String[] saved_s3_configs = null;
    String secret_key = null;
    String access_key = null;
    String endpoint = null;
    String bucket = null;
    String region = null;

    void messageParser(String message) {
        System.out.print(message);
    }

    void mainmenu() {

        for (int i = 0; i != 20; i++) {
            messageParser("\n");
        }
        messageParser("\n------------------------------------------------");
        messageParser("\n           Cloud Explorer GET request.");
        messageParser("\n------------------------------------------------");

    }

    void loadS3credentials() {
        try {
            for (String what : saved_s3_configs) {
                if (what == null) {
                    messageParser("\nError: an S3 config was null");
                    System.exit(-1);
                }
            }

            access_key = saved_s3_configs[0];
            secret_key = saved_s3_configs[1];
            endpoint = saved_s3_configs[2] + ":" + saved_s3_configs[3];
            region = saved_s3_configs[4];
        } catch (Exception loadS3Credentials) {
        }
    }

    String loadConfig(String what) {
        String data = null;

        try {
            FileReader fr = new FileReader(what);
            BufferedReader bfr = new BufferedReader(fr);
            String read = null;

            while ((read = bfr.readLine()) != null) {
                data = data + read;
            }
        } catch (Exception loadConfig) {
        }
        String remove_null = data.replace("null", "");
        String remove_symbol = remove_null.replace("@", " ");
        return remove_symbol;
    }

    void start(String Aget_file, String Abucket) {
        bucket = Abucket;
        get_file = Aget_file;

        mainmenu();

        try {
            File s3config = new File(s3_config_file);
            if (s3config.exists()) {
            } else {
                messageParser("\nError: Build config file not found.");
            }

            saved_s3_configs = loadConfig(this.s3_config_file).toString().split(" ");
            loadS3credentials();

            if (get_file != null) {

                new Thread(new Runnable() {
                    public void run() {
                        getFromS3();
                    }
                }).start();

            } else {
                messageParser("\n\n\nError: no file specified to GET.\n\n\n");
            }
        } catch (Exception Start) {
        }

    }

    String convertObject(String what, String operation) {

        if (what.contains("/")) {
            what = what.replace("/", File.separator);
        }

        if (what.contains("\\")) {
            what = what.replace("\\", File.separator);
        }

        int count = 0;
        int slash_counter = 0;
        String out_file = null;
        int another_counter = 0;

        for (int y = 0; y != what.length(); y++) {
            if (what.substring(y, y + 1).contains(File.separator)) {
                slash_counter++;
                another_counter = y;
            }
        }

        for (int y = 0; y != what.length(); y++) {
            if (y == another_counter) {
                if (operation.contains("download")) {
                    if (what.contains(File.separator)) {
                        out_file = (what.substring(y, what.length()));
                    } else {
                        out_file = (what);
                    }
                } else {
                    out_file = (what.substring(y + 1, what.length()));
                }
            }
        }
        return out_file;
    }

    void getFromS3() {
        try {
            NewJFrame.perf = true;
            System.out.print("\n\nDownloading " + get_file + "........");
            String new_object_name = convertObject(get_file, "download");

            get = new Get(get_file, access_key, secret_key, bucket, endpoint, Home + File.separator + new_object_name, null);
            Get.debug = true;
            get.startc(get_file, access_key, secret_key, bucket, endpoint, Home + File.separator + new_object_name, null);
            File check = new File(Home + File.separator + get_file);
            if (check.exists()) {
                System.out.print("\n\nGET operation Complete. Saved to: " + Home + File.separator +new_object_name + ". \n\n\n");
            } else {
                System.out.print("\n\nError: GET did not complete successfully.\n\n\n");
            }
        } catch (Exception send) {
            System.out.print("\n\nAn Error has occured while downloading the file");
            System.exit(-1);
        }
    }
}
