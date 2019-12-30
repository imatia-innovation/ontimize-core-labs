package com.ontimize.util.logging;

import java.rmi.Remote;
import java.util.List;

import org.slf4j.Logger;

import com.ontimize.util.remote.BytesBlock;

public interface IRemoteLogManager extends Remote {

	public List<Logger> getLoggerList(String sessionId) throws Exception;

	public Logger getLogger(String name, String sessionId) throws Exception;

	public Level getLevel(Logger logger, String sessionId) throws Exception;

	public void setLevel(Logger logger, Level level, String sessionId) throws Exception;

	public BytesBlock getFileLogger(String sessionId) throws Exception;
}
