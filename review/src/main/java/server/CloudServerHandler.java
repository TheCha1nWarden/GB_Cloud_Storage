package server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import serial.*;
import server.auth.BaseAuthService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CloudServerHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private Path pathServer;
    private BaseAuthService baseAuthService;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        pathServer = Paths.get("data");
        baseAuthService = new BaseAuthService();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
            switch (cloudMessage.getType()) {
                case FILE_REQUEST:
                    processFileRequestMessage((FileRequestMessage) cloudMessage, ctx);
                    break;
                case FILE:
                    processFileMessage((FileMessage) cloudMessage);
                    sendList(ctx);
                    break;
                case CHANGE_PATH_REQUEST:
                    processChangePathRequestMessage((ChangePathRequestMessage) cloudMessage, ctx);
                    break;
                case REGISTRATION:
                    processRegistration((RegistrationMessage) cloudMessage, ctx);
                    sendList(ctx);
                    break;
                case SIGN_IN_REQUEST:
                    processSignIn((SignInRequestMessage) cloudMessage, ctx);
                    break;
                case DELETE_FIlE_REQUEST:
                    processDeleteFileRequestMessage((DeleteFileRequestMessage) cloudMessage, ctx);
                    sendList(ctx);
                    break;
            }
    }

    private void processDeleteFileRequestMessage(DeleteFileRequestMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        Files.delete(pathServer.resolve(cloudMessage.getFileName()));
    }

    private void processSignIn(SignInRequestMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        String nameDir = baseAuthService.getNickByLoginPass(cloudMessage.getLogin(), cloudMessage.getPassword());
        if (nameDir != null) {
            pathServer = pathServer.resolve(nameDir);
            ctx.writeAndFlush(new ResponseSignInMessage(true));
            sendList(ctx);
        } else {
            ctx.writeAndFlush(new ResponseSignInMessage(false));

        }

    }

    private void processRegistration(RegistrationMessage cloudMessage, ChannelHandlerContext ctx) throws IOException {
        baseAuthService.registration(cloudMessage.getLogin(), cloudMessage.getPassword(), cloudMessage.getNick());
        pathServer = pathServer.resolve(cloudMessage.getNick());
        Files.createDirectory(pathServer);
        ctx.writeAndFlush(new ResponseSignInMessage(true));
    }

    private void sendList(ChannelHandlerContext ctx) throws IOException {
        ctx.writeAndFlush(new ListMessage(pathServer));
    }

    private void processChangePathRequestMessage(ChangePathRequestMessage cloudMessage, ChannelHandlerContext ctx) {
        Path targetDir = pathServer.resolve(cloudMessage.getDirName()).normalize();
        if (Files.isDirectory(targetDir) && !targetDir.equals(Paths.get("data"))) {
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
