package com.clients;

import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.qameta.allure.Step;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;


public class AmazonKinesisClient {
    AmazonKinesis amazonKinesis;

    /**
     * This method gets the Amazon kinesis client
     *
     * @return amazon kinesis client
     */
    @Step
    public AmazonKinesis getKinesisClient() {
        // Builds the Kinesis client to use the AWS client with local config
        EndpointConfiguration endpointConfiguration = new EndpointConfiguration(System.getProperty("kinesisEndpoint"), Regions.US_EAST_1.getName());
        amazonKinesis = AmazonKinesisClientBuilder
                .standard()
                .withEndpointConfiguration(endpointConfiguration)
                .build();
        return amazonKinesis;
    }

    /**
     * Get the available shards from the kinesis client
     *
     * @param streamName Name of the stream from where the shards to be accessed
     * @return the list of shards from the given stream
     */
    @Step
    public List<Shard> getShardsFromStream(String streamName) {
        DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
        //Set Stream name to describe stream request
        describeStreamRequest.setStreamName(streamName);
        List<Shard> shards = new ArrayList<>();
        String startShardId = null;
        do {
            describeStreamRequest.setExclusiveStartShardId(startShardId);
            DescribeStreamResult describeStreamResult = amazonKinesis.describeStream(describeStreamRequest);
            //Add all shards to the list
            shards.addAll(describeStreamResult.getStreamDescription().getShards());
            if (describeStreamResult.getStreamDescription().getHasMoreShards() && shards.size() > 0) {
                startShardId = shards.get(shards.size() - 1).getShardId();
            }
        } while (startShardId != null);
        return shards;
    }

    /**
     * Get the shard iterator
     *
     * @param streamName Name of the streamName from where the shards to be accessed
     * @param shards     list od shards
     * @return the list of shards from the given streamName
     */
    @Step("Name of the streamName {streamName} from where the shards to be accessed")
    public String getShardIterator(String streamName, List<Shard> shards) {
        GetShardIteratorRequest getShardIteratorRequest = new GetShardIteratorRequest();
        //set shard iterator type,steam name
        getShardIteratorRequest.setStreamName(streamName);
        getShardIteratorRequest.setShardId(shards.get(0).getShardId());
        getShardIteratorRequest.setShardIteratorType("TRIM_HORIZON");
        GetShardIteratorResult getShardIteratorResult = amazonKinesis.getShardIterator(getShardIteratorRequest);
        // get the shard iterator
        return getShardIteratorResult.getShardIterator();
    }

    /**
     * Get the records result
     *
     * @param shardIterator shard iterator
     * @return all the records from a shard
     */
    @Step
    public GetRecordsResult getRecordsResult(String shardIterator) {
        // Create new GetRecordsRequest with existing shardIterator.
        GetRecordsRequest getRecordsRequest = new GetRecordsRequest();
        getRecordsRequest.setShardIterator(shardIterator);
        // use the shard iterator to fetch all the records.
        return amazonKinesis.getRecords(getRecordsRequest);
    }

    /**
     * Get the records and validate with header with "uuid" of records from the stream
     *
     * @param headerValue Name of the header
     * @param result get records result
     * @return boolean value whether record is present in the stream
     */
    @Step("Get the records and validate with header {headerValue} with \"uuid\" of records from the stream")
    public boolean getRecordsAndValidateWithHeader(String headerValue, GetRecordsResult result) {
        List<Record> records;
        JsonObject jsonObject;
        // Put result into record list. Result may be empty.
        records = result.getRecords();
        // Display records
        for (Record record : records) {
            ByteBuffer byteBuffer = record.getData();
            //converting the record data to JSON format
            JsonParser parser = new JsonParser();
            jsonObject = parser.parse(new String(byteBuffer.array())).getAsJsonObject();
            //validate header value with uuid value in the record from a respective stream, if it's present returns true
            if (jsonObject.get("uuid").getAsString().equals(headerValue)) {
                return true;
            }
        }
        return false;
    }
}
