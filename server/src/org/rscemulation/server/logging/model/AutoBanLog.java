package org.rscemulation.server.logging.model;

public class AutoBanLog
 extends
  Log
{
 private final String reason;
 public AutoBanLog(long user, int account, String ip, String reason)
 {
  super(user, account, ip);
  this.reason = reason;
 }
 
 public String getReason()
 {
  return reason;
 }
}