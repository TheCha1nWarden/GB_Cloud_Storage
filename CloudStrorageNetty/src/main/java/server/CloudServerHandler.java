package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import serial.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path pathServer;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        pathServer = Paths.get("data");
        sendList(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        switch (cloudMessage.getType()) {
            case FILE_REQUEST:
                processFileRequestMessage((FileRequestMessage)cloudMessage, ctx);
                break;
            case FILE:
                processFileMessage((FileMessage)cloudMessage);
                sendList(ctx);
                break;
            case CHANGE_PATH_REQUEST:
                processChangePathRequest((ChangePathRequest)cloudMessage, ctx);
                break;
        }
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(pathServer));
    }

    private void processChangePathRequest(ChangePathRequest cloudMessage, ChannelHandlerContext ctx) {
        Path targetDir = pathServer.resolve(cloudMessage.getDirName()).normalize();
        if (Files.isDirectory(targetDir) && !targetDir.toAbsolutePath().toString().equals(Paths.get("data").toAbsolutePath().toString())) {
            pathServer = targetDir;
            ctx.writeAndFlush(new ListMessage(pathServer));
        }
    }

    private void processFileMessage(FileMessage cloudMessage) throws IOException {
        Files.write(pathServer.resolve(cloudMessage.getFileName()), cloudMessage.getBytes());
    }

    private void processFileRequestMessage(FileRequestMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Path path = pathServer.resolve(cloudMessage.getFileName());
        ctx.writeAndFlush(new FileMessage(path));
    }


}
