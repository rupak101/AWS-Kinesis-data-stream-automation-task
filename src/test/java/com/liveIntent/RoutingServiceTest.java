package com.liveIntent;

import com.amazonaws.services.kinesis.model.GetRecordsResult;
import com.amazonaws.services.kinesis.model.Shard;
import com.base.BaseTest;
import com.clients.AmazonKinesisClient;
import com.clients.RoutingServiceClient;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.restassured.http.Headers;
import org.apache.http.HttpStatus;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static com.Constants.Header.X_TRANSACTION_ID;
import static com.Constants.StreamName.LI_STREAM_EVEN;
import static com.Constants.StreamName.LI_STREAM_ODD;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@Owner("Rupak Mansingh")
@Feature("Tests for covering the routing service for a fictional router system (GET /query) to AWS kinesis system")
public class RoutingServiceTest extends BaseTest {

    private static final String invalidSeed = "abc";

    RoutingServiceClient routingServiceClient = new RoutingServiceClient();
    AmazonKinesisClient amazonKinesisClient = new AmazonKinesisClient();

    @DataProvider(name = "seed", parallel = true)
    private Object[][] seed() {
        return new Object[][]{
                {1}, {3}, {4}, {100}, {30021}
        };
    }

    @Test(dataProvider = "seed")
    @Description("Get routing services with a valid seed param and a message is sent to respective stream name \"li-odd-stream\" or \"li-even-stream\" ")
    public void getRoutingServicesWithValidSeed(int seed) {
        Headers headers = getHeader(seed);
        String transactionHeaderValue = headers.get("X-Transaction-Id").getValue();
        //Assert response header have the transaction id
        assertThat("Custom header is not added to the response", headers.hasHeaderWithName(X_TRANSACTION_ID), is(true));
        assertThat("Custom header is null", headers.get(X_TRANSACTION_ID), is(notNullValue()));

        amazonKinesisClient.getKinesisClient();
        //Assert records is in correct stream
        assertThat("Message is not sent to correct stream", getShardsAndValidateRecord(transactionHeaderValue, seed), is(true));
    }

    @Test
    @Description("Get routing services for invalid seed")
    public void getRoutingServiceWithInvalidSeed() {
        routingServiceClient.getRoutingService(invalidSeed)
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    private Headers getHeader(int seed) {
        return routingServiceClient.getRoutingService(String.valueOf(seed))
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .headers();
    }

    private boolean getShardsAndValidateRecord(String headerValue, int seed) {
        String streamName;
        //checking seed is even or odd
        if (seed % 2 == 0) {
            streamName = LI_STREAM_EVEN;
        } else {
            streamName = LI_STREAM_ODD;
        }
        //fetch shards from given stream
        List<Shard> shards = amazonKinesisClient.getShardsFromStream(streamName);
        //Get the shard iterator
        String shardIterator = amazonKinesisClient.getShardIterator(streamName, shards);
        GetRecordsResult result = amazonKinesisClient.getRecordsResult(shardIterator);
        //Validate the record in correct correct stream and return true, if it's present or else false
        return amazonKinesisClient.getRecordsAndValidateWithHeader(headerValue, result);
    }
}