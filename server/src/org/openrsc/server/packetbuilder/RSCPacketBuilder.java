/*    */ package org.openrsc.server.packetbuilder;
/*    */ 
/*    */ import org.openrsc.server.net.RSCPacket;
/*    */ 
/*    */ public class RSCPacketBuilder extends StaticPacketBuilder
/*    */ {
/*    */   private int pID;
/*    */ 	public RSCPacketBuilder()
{
	
}
/*    */   public RSCPacketBuilder(int pID)
/*    */   {
/*  7 */     this.pID = pID;
/*    */   }
/*    */   public RSCPacketBuilder setID(int pID) {
/* 10 */     this.pID = pID;
/*    */ 
/* 12 */     return this;
/*    */   }
/*    */  
/*    */   public RSCPacket toPacket() {
/* 16 */     byte[] data = new byte[this.curLength];
/* 17 */     System.arraycopy(this.payload, 0, data, 0, this.curLength);
/* 18 */     return new RSCPacket(this.pID, data, this.bare);
/*    */   }
/*    */ }