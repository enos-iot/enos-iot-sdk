package com.enosiot.enos.iot_http_sdk.file;

import static com.enosiot.enos.iot_mqtt_sdk.core.internals.constants.FormDataConstants.ENOS_FILE;

import java.io.File;
import java.io.IOException;

import com.enosiot.enos.iot_http_sdk.HttpConnection;
import com.enosiot.enos.iot_mqtt_sdk.message.upstream.tsl.UploadFileInfo;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import lombok.NonNull;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.MultipartBody.Part;
import okhttp3.RequestBody;

/**
 * This class defines helper function to generate form-data for files
 * @author shenjieyuan
 */
public class FileFormData
{
    static StringBuilder appendQuotedString(StringBuilder target, String key)
    {
        target.append('"');
        for (int i = 0, len = key.length(); i < len; i++)
        {
            char ch = key.charAt(i);
            switch (ch)
            {
            case '\n':
                target.append("%0A");
                break;
            case '\r':
                target.append("%0D");
                break;
            case '"':
                target.append("%22");
                break;
            default:
                target.append(ch);
                break;
            }
        }
        target.append('"');
        return target;
    }
    
    
    public static String md5(File file) throws IOException
    {
        HashCode md5 = Files.hash(file, Hashing.md5());
        return md5.toString();
    }

    public static Part createFormData(@NonNull UploadFileInfo fileInfo) throws IOException
    {
        StringBuilder disposition = new StringBuilder("form-data; name=");
        appendQuotedString(disposition, ENOS_FILE);

        disposition.append("; filename=");
        appendQuotedString(disposition, fileInfo.getFilename());

        Headers headers = new Headers.Builder()
                .addUnsafeNonAscii("Content-Disposition", disposition.toString())
                .addUnsafeNonAscii("Content-MD5", md5(fileInfo.getFile()))
                .build();

        return MultipartBody.Part.create(headers, 
                RequestBody.create(MediaType.parse(HttpConnection.MEDIA_TYPE_OCTET_STREAM), fileInfo.getFile()));
    }
}
