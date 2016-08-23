/**
 * Copyright (C) 2015  the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mujava.op.weak;

import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p> </p>
 * @author Yu-Seung Ma
 * @version 1.0
 */

public class Arithmetic_OP_Weak extends InstrumentationParser {
    public Arithmetic_OP_Weak(FileEnvironment file_env, CompilationUnit comp_unit) {
        super(file_env, comp_unit);
    }

    /**
     * Determine whether a given expression is of arithmetic type
     * @param p
     * @return
     * @throws ParseTreeException
     */
    public boolean isArithmeticType(Expression p) throws ParseTreeException {
        OJClass type = getType(p);
        if (type == OJSystem.INT || type == OJSystem.DOUBLE || type == OJSystem.FLOAT
                || type == OJSystem.LONG || type == OJSystem.SHORT
                || type == OJSystem.CHAR || type == OJSystem.BYTE) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether a given expression has a binary arithmetic operator
     * @param p
     * @return
     * @throws ParseTreeException
     */
    public boolean hasBinaryArithmeticOp(BinaryExpression p) throws ParseTreeException {
        int op_type = p.getOperator();
        if ((op_type == BinaryExpression.TIMES) || (op_type == BinaryExpression.DIVIDE)
                || (op_type == BinaryExpression.MOD) || (op_type == BinaryExpression.PLUS)
                || (op_type == BinaryExpression.MINUS))
            return true;
        else
            return false;
    }
}
