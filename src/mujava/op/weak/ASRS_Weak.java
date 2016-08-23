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
 * <p>Generate ASRS (Assignment Operator Replacement (short-cut)) mutants --
 *    replace each occurrence of one of the assignment operators 
 *    (+=, -+, *=, /=, %=, &=, |=, ^=, <<=, >>=, >>>=) by each of the 
 *    other operators  
 * </p>
 * @author Haoyuan Sun
 * @version 0.1a
 */

/* Each occurrence of one of the assignment operators
 *    (+=, -+, *=, /=, %=, &=, |=, ^=, <<=, >>=, >>>=),
 *    is replaced by each of the other operators 
 * 
 */

public class ASRS_Weak extends InstrumentationMutator {
    public ASRS_Weak(FileEnvironment file_env, ClassDeclaration cdecl, CompilationUnit comp_unit) {
        super(file_env, comp_unit);
    }

    /**
     * If the assignment operator is of arithmetic type (+=, -=, *=, /=, %=),
     *    replace it with each of the other arithmetic assignment operators.
     * If the assignment operator is of logical type (&=, |=, ^=),
     *    replace it with each of the other logical assignment operators.
     * If the assignment operator is a shift operator (<<, >>, >>>)
     *    replace it with each of the other shift operators.
     */
    public void visit(AssignmentExpression p) throws ParseTreeException {
        mutExpression = p;

        OJClass type = getType(p);

        int op = p.getOperator();
        if ((op == AssignmentExpression.ADD) || (op == AssignmentExpression.SUB) ||
                (op == AssignmentExpression.MULT) || (op == AssignmentExpression.DIVIDE) ||
                (op == AssignmentExpression.MOD)) {
            genArithmeticMutants(p, op, type);
        } else if ((op == AssignmentExpression.AND) || (op == AssignmentExpression.OR) ||
                (op == AssignmentExpression.XOR)) {
            genLogicalMutants(p, op, type);
        } else if ((op == AssignmentExpression.SHIFT_L) || (op == AssignmentExpression.SHIFT_R) ||
                (op == AssignmentExpression.SHIFT_RR)) {
            genShiftMutants(p, op, type);
        }

        mutExpression = null;
    }

    /*
     * Replace the arithmetic assignment operator (+=, -+, *=, /=, %=)
     * by each of the other operators
     */
    void genArithmeticMutants(AssignmentExpression p, int op, OJClass type) {
        if (!(op == AssignmentExpression.ADD)) {
            genInstrument(p, AssignmentExpression.ADD, type);
        }
        if (!(op == AssignmentExpression.DIVIDE)) {
            genInstrument(p, AssignmentExpression.DIVIDE, type);
        }
        if (!(op == AssignmentExpression.MULT)) {
            genInstrument(p, AssignmentExpression.MULT, type);
        }
        if (!(op == AssignmentExpression.SUB)) {
            genInstrument(p, AssignmentExpression.SUB, type);
        }
        if (!(op == AssignmentExpression.MOD)) {
            genInstrument(p, AssignmentExpression.MOD, type);
        }
    }

    /*
     * Replace the logical assignment operator (&=, |+, ^=)
     * by each of the other operators
     */
    void genLogicalMutants(AssignmentExpression p, int op, OJClass type) {
        if (!(op == AssignmentExpression.AND)) {
            genInstrument(p, AssignmentExpression.AND, type);
        }
        if (!(op == AssignmentExpression.OR)) {
            genInstrument(p, AssignmentExpression.OR, type);
        }
        if (!(op == AssignmentExpression.XOR)) {
            genInstrument(p, AssignmentExpression.XOR, type);
        }
    }

    /*
     * Replace the shift assignment operator (<<=, >>=, >>>=)
     * by each of the other operators
     */
    void genShiftMutants(AssignmentExpression p, int op, OJClass type) {
        if (!(op == AssignmentExpression.SHIFT_L)) {
            genInstrument(p, AssignmentExpression.SHIFT_L, type);
        }
        if (!(op == AssignmentExpression.SHIFT_R)) {
            genInstrument(p, AssignmentExpression.SHIFT_R, type);
        }
        if (!(op == AssignmentExpression.SHIFT_RR)) {
            genInstrument(p, AssignmentExpression.SHIFT_RR, type);
        }
    }


    public void genInstrument(AssignmentExpression original, int op_val, OJClass type) {
        Instrument inst = new Instrument();

        // store the right hand side in a tmp variable
        inst.init.add(new VariableDeclaration(TypeName.forOJClass(type), 
                    InstConfig.varPrefix + "RHS", original.getRight()));

        // compute the original expression
        String op = opString[original.getOperator()];
        BinaryExpression a = new BinaryExpression(original.getLeft(), op, 
                new Variable(InstConfig.varPrefix + "RHS"));
        inst.init.add(new VariableDeclaration(TypeName.forOJClass(type), 
                    InstConfig.varPrefix + "ORIGINAL", a));

        // compute the mutated expression
        op = opString[op_val];
        BinaryExpression b = new BinaryExpression(original.getLeft(), op, 
                new Variable(InstConfig.varPrefix + "RHS"));
        inst.init.add(new VariableDeclaration(TypeName.forOJClass(type), 
                    InstConfig.varPrefix + "MUTANT", b));

        inst.addAssertion(InstConfig.varPrefix + "ORIGINAL",
                InstConfig.varPrefix + "MUTANT");

        // assign the value to the left hand side
        inst.varName = InstConfig.varPrefix + "ORIGINAL";

        outputToFile(inst);
    }

    // adapted from openjava's source code
    private static String[] opString = {"", "*", "/", "%", "+", "-", 
        "<<", ">>", ">>>", "&", "^", "|"};

    /**
     * Output ASRS mutants to file
     * @param inst
     */
    public void outputToFile(Instrument inst) {
        if (comp_unit == null)
            return;

        String f_name;
        num++;
        f_name = getSourceName("ASRS");
        String mutant_dir = getMuantID("ASRS");

        try {
            PrintWriter out = getPrintWriter(f_name);
            //PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
            InstrumentationCodeWriter writer =
                    new InstrumentationCodeWriter(mutant_dir, out);

            writer.setEnclose(encBlock);
            writer.setBlock(mutBlock);
            writer.setStatement(mutStatement);
            writer.setExpression(mutExpression);
            writer.setInstrument(inst);
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
