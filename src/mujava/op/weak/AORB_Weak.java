/**
 * Copyright (C) 2016 the original author or authors.
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

import java.io.*;

/**
 * <p>Generate AORB (Arithmetic Operator Replacement (Binary)) mutants -- 
 *    replace an arithmetic operator by each of the other operators  
 *    (*, /, %, +, -)
 * </p>
 * @author Haoyuan SUn
 * @version 0.1a
 */

public class AORB_Weak extends Arithmetic_OP_Weak {
    public AORB_Weak(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
        super(file_env, comp_unit);
    }

    /**
     * Mutate the arithmetic operator to TIMES, DIVIDE,
     * MOD, PLUS, MINUS (excluding itself)
     */
    public void visit(BinaryExpression p) throws ParseTreeException {
        // first recursively visit the parse tree
        super.visit(p);

        if (mutExpression == null) mutExpression = p;
        
        // mutate the current binary expression
        if (isArithmeticType(p)) {
            int op_type = p.getOperator();
            switch (op_type) {
                // for AOR mutation operator
                // 5 Arithmetic Operators : TIMES, DIVIDE, MOD, PLUS, MINUS
                case BinaryExpression.TIMES:
                    aorMutantGen(p, BinaryExpression.TIMES);
                    break;

                case BinaryExpression.DIVIDE:
                    aorMutantGen(p, BinaryExpression.DIVIDE);
                    break;

                case BinaryExpression.MOD:
                    aorMutantGen(p, BinaryExpression.MOD);
                    break;

                case BinaryExpression.PLUS:
                    aorMutantGen(p, BinaryExpression.PLUS);
                    break;

                case BinaryExpression.MINUS:
                    aorMutantGen(p, BinaryExpression.MINUS);
                    break;
            }
        }

        if (mutExpression.getObjectID() == p.getObjectID()) mutExpression = null;
    }

    private void aorMutantGen(BinaryExpression exp, int op) throws ParseTreeException {
        BinaryExpression original = new BinaryExpression(genVar(counter+3), op, genVar(counter+2));
        BinaryExpression mutant = (BinaryExpression) (original.makeRecursiveCopy());

        // original
        typeStack.add(getType(exp));
        exprStack.add(original); // +0
        // mutant
        typeStack.add(getType(exp));
        exprStack.add(mutant); // +1
        // RHS
        typeStack.add(getType(exp.getRight()));
        exprStack.add(exp.getRight()); // +2
        // LHS
        typeStack.add(getType(exp.getLeft()));
        exprStack.add(exp.getLeft()); // +3
        counter += 4;

        if (op != BinaryExpression.TIMES) {
            mutant.setOperator(BinaryExpression.TIMES);

            outputToFile();
        }

        if (op != BinaryExpression.DIVIDE) {
            mutant.setOperator(BinaryExpression.DIVIDE);

            outputToFile();
        }

        if (op != BinaryExpression.MOD) {
            mutant.setOperator(BinaryExpression.MOD);

            outputToFile();
        }

        if (op != BinaryExpression.PLUS) {
            mutant.setOperator(BinaryExpression.PLUS);

            outputToFile();
        }
        
        if (op != BinaryExpression.MINUS) {
            mutant.setOperator(BinaryExpression.MINUS);

            outputToFile();
        }
        
        pop(4);
    }

    /**
     * Output AORB mutants to file
     */
    public void outputToFile() {
        if (comp_unit == null)
            return;

        String f_name;
        num++;
        f_name = getSourceName("AORB");
        String mutant_dir = getMuantID("AORB");

        try {
            PrintWriter out = getPrintWriter(f_name);
            //PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
            InstrumentationCodeWriter writer = new InstrumentationCodeWriter(mutant_dir, out);

            writer.setEnclose(encBlock);
            writer.setBlock(mutBlock);
            writer.setStatement(mutStatement);
            writer.setExpression(mutExpression);
            writer.setInstrument(genInstrument());
            writer.setMethodSignature(currentMethodSignature);

            comp_unit.accept(writer);
            out.flush();
            out.close();
        } catch (IOException e) {
            System.err.println("fails to create " + f_name);
        } catch (ParseTreeException e) {
            System.err.println("errors during printing " + f_name);
            e.printStackTrace();
        }
    }
}
