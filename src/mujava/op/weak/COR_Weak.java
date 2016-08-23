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
 * <p>Generate COR (Conditional Operator Replacement mutants --
 *    replace each logical operator by each of the other operators 
 *    (and-&&, or-||, and with no conditional evaluation-&, 
 *    or with no conditional evaluation-|, not equivalent-^)    
 * </p>
 * @author Haoyuan Sun
 * @version 0.1a
 */

public class COR_Weak extends InstrumentationParser {
    public COR_Weak (FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
        super(file_env, comp_unit);
    }

    /**
     * If the operator is one of logical operators, replace it with
     * each of the other logical operators
     */
    public void visit(BinaryExpression p) throws ParseTreeException {
        // first recursively search down the parse tree
        super.visit(p);

        // mutate the current binary operator
        if (mutExpression == null) mutExpression = p;

        if ((getType(p.getLeft()) == OJSystem.BOOLEAN) &&
                (getType(p.getRight()) == OJSystem.BOOLEAN)) {
            int op_type = p.getOperator();
            if ((op_type == BinaryExpression.LOGICAL_AND) ||
                    (op_type == BinaryExpression.LOGICAL_OR) ||
                    (op_type == BinaryExpression.BITAND) ||
                    (op_type == BinaryExpression.BITOR) ||
                    (op_type == BinaryExpression.XOR)) {
                corMutantGen(p, op_type);
            }
        }

        if (mutExpression.getObjectID() == p.getObjectID()) mutExpression = null;
    }

    private void corMutantGen(BinaryExpression exp, int op) throws ParseTreeException{
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
        if ((op != BinaryExpression.LOGICAL_AND) && (op != BinaryExpression.BITAND)) {
            mutant.setOperator(BinaryExpression.LOGICAL_AND);

            outputToFile();
        }

        if ((op != BinaryExpression.LOGICAL_OR) && (op != BinaryExpression.BITOR)) {
            mutant.setOperator(BinaryExpression.LOGICAL_OR);

            outputToFile();
        }

        if (op != BinaryExpression.XOR) {
            mutant.setOperator(BinaryExpression.XOR);

            outputToFile();
        }

        pop(4);
    }

    /**
     * Output COR mutants to files
     */
    public void outputToFile() {
        if (comp_unit == null)
            return;

        String f_name;
        num++;
        f_name = getSourceName("COR");
        String mutant_dir = getMuantID("COR");

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
