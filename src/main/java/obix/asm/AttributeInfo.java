/*
 * This code licensed to public domain
 */
package obix.asm;

/**
 * @author    Brian Frank
 * @creation  15 Mar 00
 * @version   $Revision: 1$ $Date: 6/21/00 2:32:06 PM$
 */
public class AttributeInfo
{  

  public AttributeInfo(Assembler asm, int name, byte[] info)
  {
    this.asm  = asm;
    this.name = name;
    this.info = info;
  }

  public AttributeInfo(Assembler asm, String name, byte[] info)
  {
    this.asm  = asm;
    this.name = asm.cp.utf(name);
    this.info = info;
  }

  public AttributeInfo(Assembler asm, String name, String value)
  {
    this.asm  = asm;
    this.name = asm.cp.utf(name);
        
    int v = asm.cp.utf(value);
    this.info = new byte[2];
    info[0] = (byte)((v >>> 8) & 0xFF);
    info[1] = (byte)((v >>> 0) & 0xFF);
  }

  public AttributeInfo(Assembler asm, int name)
  {
    this.asm  = asm;
    this.name = name;
  }

  public AttributeInfo(Assembler asm, String name)
  {
    this.asm  = asm;
    this.name = asm.cp.utf(name);
  }

  void compile(Buffer buf)
  {
    buf.u2(name);
    buf.u4(info.length);
    buf.append(info);
  }

  private static byte[] EMPTY = new byte[0];

  public final Assembler asm;
  public final int name;
  public byte[] info = EMPTY;
}
