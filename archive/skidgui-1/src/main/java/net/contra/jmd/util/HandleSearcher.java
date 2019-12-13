package net.contra.jmd.util;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.LDC;

/**
 * Created by IntelliJ IDEA.
 * User: Eric
 * Date: Nov 25, 2010
 * Time: 10:16:26 PM
 */
public class HandleSearcher {
    InstructionHandle[] handles;
    ConstantPoolGen cpg;
    public int index;

    public void setPosition(int index) {
        this.index = index;
    }

    public LDC previousLDC() {
        for (; index >= 0; index--) {

            if (handles[index].getInstruction() instanceof LDC) {
                return (LDC) handles[index].getInstruction();
            }

        }

        return null;
    }

    public INVOKESTATIC nextInvokeStatic(String className) {
        for (; index < handles.length; index++) {
            if (index > -1) {
                if (handles[index].getInstruction() instanceof INVOKESTATIC) {

                    INVOKESTATIC methodCall = (INVOKESTATIC) handles[index].getInstruction();

                    if (methodCall.getClassName(cpg).equals(className)) {
                        return methodCall;
                    }

                }
            }

        }

        return null;
    }

}
