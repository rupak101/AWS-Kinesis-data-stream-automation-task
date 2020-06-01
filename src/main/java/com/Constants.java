package com;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Constants {

    @NoArgsConstructor(access = PRIVATE)
    public static class StreamName {
        public static final String LI_STREAM_EVEN = "li-stream-even";
        public static final String LI_STREAM_ODD = "li-stream-odd";
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class Header {
        public static final String X_TRANSACTION_ID = "X-Transaction-Id";
    }
}
