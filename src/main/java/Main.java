import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.FileOutputStream;
import java.io.InputStream;

public class Main {
    private static final String API_KEY = "TODO: PASTE TOKEN HERE!!!!!!";

    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try (
                CloseableHttpClient client = HttpClientBuilder.create()
                        .setDefaultRequestConfig(
                                RequestConfig.custom()
                                .setConnectTimeout(5000)
                                .setSocketTimeout(30000)
                                .setRedirectsEnabled(true)
                                .build()
                        )
                        .build()
        ) {
            NasaApiResponse responseData;

            try (
                    CloseableHttpResponse response = client.execute(
                            new HttpGet("https://api.nasa.gov/planetary/apod?api_key=" + API_KEY)
                    );

                    InputStream content = response.getEntity().getContent();
            ){
                responseData = mapper.readValue(
                        content.readAllBytes(),
                        new TypeReference<>() {}
                );
            }

            String url = responseData.getHdurl();
            String filePath = url.substring(url.lastIndexOf("/") + 1);

            try (
                    CloseableHttpResponse response = client.execute(new HttpGet(url));
                    InputStream content = response.getEntity().getContent();
                    FileOutputStream outputStream = new FileOutputStream(filePath);
            ) {
                content.transferTo(outputStream);
            }

            System.out.printf("Downloaded '%s' successfully!\n", filePath);
        }
    }
}
