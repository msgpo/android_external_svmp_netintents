/*
 * Copyright 2013 The MITRE Corporation, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mitre.svmp.net_intents;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import org.mitre.svmp.protocol.SVMPProtocol.Request;
import java.util.concurrent.BlockingQueue;

/**
 * @author Colin Courtney
 */
public class NettyClientInitializer extends ChannelInitializer<SocketChannel> {

    protected BlockingQueue<Request> receiveQueue;
    public NettyClientInitializer(BlockingQueue<Request> receiveQueue) {
        this.receiveQueue = receiveQueue;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // create a new ChannelPipeline to deal with channel data
        ChannelPipeline p = ch.pipeline();

        // add decoders to the pipeline to receive data from the server
        p.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
        p.addLast("protobufDecoder", new ProtobufDecoder(Request.getDefaultInstance()));

        // add encoders to the pipeline to send data to the server
        p.addLast("frameEncoder", new ProtobufVarint32LengthFieldPrepender());
        p.addLast("protobufEncoder", new ProtobufEncoder());

        // add the handler to the pipeline to construct/send data
        p.addLast("handler", new NettyClientHandler(receiveQueue));
    }
}
