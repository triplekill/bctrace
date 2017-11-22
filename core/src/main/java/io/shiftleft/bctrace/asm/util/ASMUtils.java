/*
 * ShiftLeft, Inc. CONFIDENTIAL
 * Unpublished Copyright (c) 2017 ShiftLeft, Inc., All Rights Reserved.
 *
 * NOTICE: All information contained herein is, and remains the property of ShiftLeft, Inc.
 * The intellectual and technical concepts contained herein are proprietary to ShiftLeft, Inc.
 * and may be covered by U.S. and Foreign Patents, patents in process, and are protected by
 * trade secret or copyright law. Dissemination of this information or reproduction of this
 * material is strictly forbidden unless prior written permission is obtained
 * from ShiftLeft, Inc. Access to the source code contained herein is hereby forbidden to
 * anyone except current ShiftLeft, Inc. employees, managers or contractors who have executed
 * Confidentiality and Non-disclosure agreements explicitly covering such access.
 *
 * The copyright notice above does not evidence any actual or intended publication or disclosure
 * of this source code, which includeas information that is confidential and/or proprietary, and
 * is a trade secret, of ShiftLeft, Inc.
 *
 * ANY REPRODUCTION, MODIFICATION, DISTRIBUTION, PUBLIC PERFORMANCE, OR PUBLIC DISPLAY
 * OF OR THROUGH USE OF THIS SOURCE CODE WITHOUT THE EXPRESS WRITTEN CONSENT OF ShiftLeft, Inc.
 * IS STRICTLY PROHIBITED, AND IN VIOLATION OF APPLICABLE LAWS AND INTERNATIONAL TREATIES.
 * THE RECEIPT OR POSSESSION OF THIS SOURCE CODE AND/OR RELATED INFORMATION DOES NOT
 * CONVEY OR IMPLY ANY RIGHTS TO REPRODUCE, DISCLOSE OR DISTRIBUTE ITS
 * CONTENTS, OR TO MANUFACTURE, USE, OR SELL ANYTHING THAT IT MAY DESCRIBE, IN WHOLE OR IN PART.
 */
package io.shiftleft.bctrace.asm.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

/**
 *
 * @author Ignacio del Valle Alles idelvall@shiftleft.io
 */
public class ASMUtils {

  public static boolean isInterface(int modifiers) {
    return (modifiers & Opcodes.ACC_INTERFACE) != 0;
  }

  public static boolean isAbstract(int modifiers) {
    return (modifiers & Opcodes.ACC_ABSTRACT) != 0;
  }

  public static boolean isNative(int modifiers) {
    return (modifiers & Opcodes.ACC_NATIVE) != 0;
  }

  public static boolean isStatic(int modifiers) {
    return (modifiers & Opcodes.ACC_STATIC) != 0;
  }

  public static boolean isPublic(int modifiers) {
    return (modifiers & Opcodes.ACC_PUBLIC) != 0;
  }

  public static boolean isProtected(int modifiers) {
    return (modifiers & Opcodes.ACC_PROTECTED) != 0;
  }

  public static boolean isPrivate(int modifiers) {
    return (modifiers & Opcodes.ACC_PRIVATE) != 0;
  }

  public static VarInsnNode getLoadInst(Type type, int position) {
    int opCode = -1;
    switch (type.getDescriptor().charAt(0)) {
      case 'B':
        opCode = Opcodes.ILOAD;
        break;
      case 'C':
        opCode = Opcodes.ILOAD;
        break;
      case 'D':
        opCode = Opcodes.DLOAD;
        break;
      case 'F':
        opCode = Opcodes.FLOAD;
        break;
      case 'I':
        opCode = Opcodes.ILOAD;
        break;
      case 'J':
        opCode = Opcodes.LLOAD;
        break;
      case 'L':
        opCode = Opcodes.ALOAD;
        break;
      case '[':
        opCode = Opcodes.ALOAD;
        break;
      case 'Z':
        opCode = Opcodes.ILOAD;
        break;
      case 'S':
        opCode = Opcodes.ILOAD;
        break;
      default:
        throw new ClassFormatError("Invalid method signature: "
                + type.getDescriptor());
    }
    return new VarInsnNode(opCode, position);
  }

  public static MethodInsnNode getWrapperContructionInst(Type type) {

    char charType = type.getDescriptor().charAt(0);
    String wrapper;
    switch (charType) {
      case 'B':
        wrapper = "java/lang/Byte";
        break;
      case 'C':
        wrapper = "java/lang/Character";
        break;
      case 'D':
        wrapper = "java/lang/Double";
        break;
      case 'F':
        wrapper = "java/lang/Float";
        break;
      case 'I':
        wrapper = "java/lang/Integer";
        break;
      case 'J':
        wrapper = "java/lang/Long";
        break;
      case 'L':
        return null;
      case '[':
        return null;
      case 'Z':
        wrapper = "java/lang/Boolean";
        break;
      case 'S':
        wrapper = "java/lang/Short";
        break;
      default:
        throw new ClassFormatError("Invalid method signature: "
                + type.getDescriptor());
    }

    return new MethodInsnNode(Opcodes.INVOKESTATIC, wrapper, "valueOf",
            "(" + charType + ")L" + wrapper + ";", false);

  }

  public static VarInsnNode getStoreInst(Type type, int position) {
    int opCode = -1;
    switch (type.getDescriptor().charAt(0)) {
      case 'B':
        opCode = Opcodes.ISTORE;
        break;
      case 'C':
        opCode = Opcodes.ISTORE;
        break;
      case 'D':
        opCode = Opcodes.DSTORE;
        break;
      case 'F':
        opCode = Opcodes.FSTORE;
        break;
      case 'I':
        opCode = Opcodes.ISTORE;
        break;
      case 'J':
        opCode = Opcodes.LSTORE;
        break;
      case 'L':
        opCode = Opcodes.ASTORE;
        break;
      case '[':
        opCode = Opcodes.ASTORE;
        break;
      case 'Z':
        opCode = Opcodes.ISTORE;
        break;
      case 'S':
        opCode = Opcodes.ISTORE;
        break;
      default:
        throw new ClassFormatError("Invalid method signature: "
                + type.getDescriptor());
    }
    return new VarInsnNode(opCode, position);
  }

  public static AbstractInsnNode getPushInstruction(int value) {

    if (value == -1) {
      return new InsnNode(Opcodes.ICONST_M1);
    } else if (value == 0) {
      return new InsnNode(Opcodes.ICONST_0);
    } else if (value == 1) {
      return new InsnNode(Opcodes.ICONST_1);
    } else if (value == 2) {
      return new InsnNode(Opcodes.ICONST_2);
    } else if (value == 3) {
      return new InsnNode(Opcodes.ICONST_3);
    } else if (value == 4) {
      return new InsnNode(Opcodes.ICONST_4);
    } else if (value == 5) {
      return new InsnNode(Opcodes.ICONST_5);
    } else if ((value >= -128) && (value <= 127)) {
      return new IntInsnNode(Opcodes.BIPUSH, value);
    } else if ((value >= -32768) && (value <= 32767)) {
      return new IntInsnNode(Opcodes.SIPUSH, value);
    } else {
      return new LdcInsnNode(value);
    }
  }

  public static byte[] toByteArray(InputStream is) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    while (true) {
      int r = is.read(buffer);
      if (r == -1) {
        break;
      }
      out.write(buffer, 0, r);
    }
    return out.toByteArray();
  }
}
