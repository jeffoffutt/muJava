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
 * <p>Generate ROR (Rational Operator Replacement) mutants --
 *    replace each occurrence of one of the relational operators 
 *    (<, <=, >, >=, =, <>) by each of the other operators 
 *    and by <i>falseOp</i> and <i>trueOp</i> where 
 *    <i>falseOp</i> always returns <i>false</i> and 
 *    <i>trueOp</i> always returns <i>true</i> 
 * </p>
 * @author Haoyuan Sun
 * @version 0.1a
 */

public class ROR_Weak extends Arithmetic_OP_Weak {
    public ROR_Weak (FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
        super(file_env, comp_unit);
    }

    public void visit(BinaryExpression p) throws ParseTreeException {
        // first recursively search down the parse tree
        super.visit(p);

        // mutate the current binary operator
        if (mutExpression == null) mutExpression = p;

        int op_type = p.getOperator();

        if (isArithmeticType(p.getLeft()) && isArithmeticType(p.getRight())) {
            // fix the fault that missed <, Lin, 050814
            if ((op_type == BinaryExpression.GREATER) || (op_type == BinaryExpression.GREATEREQUAL) ||
                    (op_type == BinaryExpression.LESSEQUAL) || (op_type == BinaryExpression.EQUAL) ||
                    (op_type == BinaryExpression.NOTEQUAL)
                    || (op_type == BinaryExpression.LESS)) {
                primitiveRORMutantGen(p, op_type);
            }
        } else if ((op_type == BinaryExpression.EQUAL) || (op_type == BinaryExpression.NOTEQUAL)) {
            objectRORMutantGen(p, op_type);
        }

        if (mutExpression.getObjectID() == p.getObjectID()) mutExpression = null;
    }

    private void primitiveRORMutantGen(BinaryExpression exp, int op) throws ParseTreeException{
        BinaryExpression original = new BinaryExpression(genVar(counter+3), op, genVar(counter+2));
        BinaryExpression mutant = (BinaryExpression) (original.makeRecursiveCopy());

        // original
        typeStack.add(OJSystem.BOOLEAN);
        exprStack.add(original); // +0
        // mutant
        typeStack.add(OJSystem.BOOLEAN);
        exprStack.add(mutant); // +1
        // RHS
        typeStack.add(getType(exp.getRight()));
        exprStack.add(exp.getRight()); // +2
        // LHS
        typeStack.add(getType(exp.getLeft()));
        exprStack.add(exp.getLeft()); // +3
        counter += 4;

        // set the binary operator for mutant
        if (op != BinaryExpression.GREATER) {
            mutant.setOperator(BinaryExpression.GREATER);

            outputToFile();
        }

        if (op != BinaryExpression.GREATEREQUAL) {
            mutant.setOperator(BinaryExpression.GREATEREQUAL);

            outputToFile();
        }

        if (op != BinaryExpression.LESS) {
            mutant.setOperator(BinaryExpression.LESS);

            outputToFile();
        }

        if (op != BinaryExpression.LESSEQUAL) {
            mutant.setOperator(BinaryExpression.LESSEQUAL);

            outputToFile();
        }

        if (op != BinaryExpression.EQUAL) {
            mutant.setOperator(BinaryExpression.EQUAL);

            outputToFile();
        }

        if (op != BinaryExpression.NOTEQUAL) {
            mutant.setOperator(BinaryExpression.NOTEQUAL);

            outputToFile();
        }

        pop(4);

        // Change entire expression to true/false

        // original
        typeStack.add(OJSystem.BOOLEAN);
        exprStack.add((BinaryExpression)exp.makeRecursiveCopy()); // +0
        // Change the expression to true
        typeStack.add(OJSystem.BOOLEAN);
        exprStack.add(Literal.constantTrue());
        counter += 2;
        outputToFile();
        pop(1);
        // Change the expression to false
        typeStack.add(OJSystem.BOOLEAN);
        exprStack.add(Literal.constantFalse());
        counter += 1;
        outputToFile();
        pop(2);

        /**
         * New implementation of ROR based on the fault hierarchies
         * fewer ROR mutants are generated
         * For details, see the paper "Better predicate testing" by Kaminski, Ammann, and Offutt at AST'11
         *
         * Not included here, consult op/basic/ROR.java
         */
    }

    private void objectRORMutantGen(BinaryExpression exp, int op) throws ParseTreeException{
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

        // set the binary operator for mutant
        if (op != BinaryExpression.EQUAL) {
            mutant.setOperator(BinaryExpression.EQUAL);

            outputToFile();
        }

        if (op != BinaryExpression.NOTEQUAL) {
            mutant.setOperator(BinaryExpression.NOTEQUAL);

            outputToFile();
        }

        pop(4);
    }

    /**
     * Output ROR mutants to files
     */
    public void outputToFile() {
        if (comp_unit == null)
            return;

        String f_name;
        num++;
        f_name = getSourceName("ROR");
        String mutant_dir = getMuantID("ROR");

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
