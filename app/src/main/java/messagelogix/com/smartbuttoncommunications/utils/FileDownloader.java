package messagelogix.com.smartbuttoncommunications.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Vahid
 * This is the util class for downloading a file to the device
 */
public class FileDownloader {

    public static final String PDF = "pdf";
    // public static Context context;

    public static class DownloadTask extends AsyncTask<String, Void, Boolean> {

        private Context context;

        public DownloadTask(Context context) {

            this.context = context;
        }

        private static final String TAG = FileDownloader.class.getSimpleName();

        public String LOG_TAG = DownloadTask.class.getSimpleName();

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... urls) {

            try {
                String extStorageDirectory = Environment.getExternalStorageDirectory()
                        .toString();
                File folder = new File(extStorageDirectory, PDF);
                folder.mkdir();
                String fileName = urls[0].substring(urls[0].lastIndexOf('/') + 1);
                Log.e(LOG_TAG, "caching filename = " + fileName);
                File file = new File(folder, fileName);
                file.createNewFile();
                FileOutputStream f = new FileOutputStream(file);
                URL u = new URL(urls[0]);
                HttpsURLConnection c = (HttpsURLConnection) u.openConnection();
                c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
                c.setRequestProperty("Accept", "*/*");
                c.setRequestMethod("GET");
                c.connect();
                InputStream in = encryptStream(c.getInputStream());
                byte[] buffer = new byte[1024];
                int len1 = 0;
                while ((len1 = in.read(buffer)) > 0) {
                    f.write(buffer, 0, len1);
                }
                Log.e(LOG_TAG, "Caching " + file.getName() + " ...");
                f.close();
                return file.exists();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public InputStream encryptStream(InputStream inputStream) throws IOException {
            // this dynamically extends to take the bytes you read
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            // this is storage overwritten on each iteration with bytes
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            // we need to know how may bytes were read to write them to the byteBuffer
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
            //Get the key from preferences
            Preferences.init(context);
            SecretKeySpec secretKey = Preferences.getSecretKeySpec();
            Cryptography cryptography = new Cryptography(secretKey);
            byte[] encodedBytes = cryptography.encryptAES(byteBuffer.toByteArray(), secretKey);
            InputStream encryptedStream = new ByteArrayInputStream(encodedBytes);
            // and then we can return your byte array.
            return encryptedStream;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result) Log.e(LOG_TAG, "File cahced");
            else Log.e(LOG_TAG, "File not Cached");
        }
    }
}
