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

import java.io.*;
import java.util.ArrayList;

import mujava.op.util.TraditionalMutantCodeWriter;
import openjava.ptree.*;


/**
 * <p>Description: Writes the mutated statement and its associated instrumentation 
 *    for weak mutation testing 
 * </p>
 * @author Haoyuan Sun
 * @version 0.1a
 */


public class InstrumentationCodeWriter extends TraditionalMutantCodeWriter {
    // some mutants are always killed or always live
    // 1 -- always killed, 2 -- always live, 0 -- run test
    private int preKill = 0;

    // indicate the outter most block which encloses the mutant
    private Statement encBlock;

    // indicate which block was mutated
    private Statement mutBlock;

    // indicate which statement was mutated
    private Statement mutStatement;

    // indicate which expression was mutated
    private Expression mutExpression;

    // instrumentation code
    private Instrument inst;

    // rename variables in for loops
    private ArrayList<String> itName;

    public InstrumentationCodeWriter(PrintWriter out) {
        super(out);
    }

    public InstrumentationCodeWriter(String mutant_dir, PrintWriter out) {
        super(mutant_dir, out);
    }

    public void setPreKill(int p) { preKill = p; }

    public void setEnclose(Statement s) { encBlock = s; }

    public void setBlock(Statement s) { mutBlock = s; }

    public void setExpression(Expression e) { mutExpression = e; }

    public void setStatement(Statement s) { mutStatement = s; }

    public void setInstrument(Instrument i) { inst = i; }

    public void visit(AssignmentExpression p) throws ParseTreeException {
        if(!isSameObject(mutExpression, p)){
            super.visit(p);
            return;
        }

        out.println();
        line_num++;
        super.visit(inst.init);
        for (String str : inst.assertion) writeString(str);
        // assignment is made here
        super.visit(inst.post);
    }

    // set a preKill flag in the first line of code
    public void visit(CompilationUnit p)
            throws ParseTreeException {
        //System.out.println("MUTANT!");
        out.println("// PREKILL: " + preKill);
        line_num++;
        out.println("// This is an instrumented mutant program.");
        line_num++;
        out.println();
        line_num++;

        /* package statement */
        String qn = p.getPackage();
        if (qn != null) {
            out.print("package " + qn + ";");
            out.println();
            line_num++;

            out.println();
            line_num++;
            out.println();
            line_num++;
        }

        /* import statement list */
        String[] islst = p.getDeclaredImports();
        for (int i = 0; i < islst.length; ++i) {
            out.println("import " + islst[i] + ";");
            line_num++;
        }

        // weak mutation kill and live
        out.println("import static mujava.op.weak.Instrument.*;");
        line_num++;

        out.println();
        line_num++;
        
        /* type declaration list */
        ClassDeclarationList tdlst = p.getClassDeclarations();
        tdlst.accept(this);
    }

    public void visit(DoWhileStatement p) throws ParseTreeException {
        // do nothing special if the mutant is not in the control predicate
        if (!isSameObject(mutStatement, p)) {
            super.visit(p);
            writeExit(p);
            return;
        }

        // otherwise, the mutant is the conditional statement of this loop

        writeTab();
        out.println("while (true) {");
        line_num++;
        pushNest();

        // body
        StatementList stmts = p.getStatements();
        if (!stmts.isEmpty()) stmts.accept(this);
        out.println();
        line_num++;

        // instrumentation
        super.visit(inst.init);
        for (String str : inst.assertion) writeString(str);
        super.visit(inst.post);

        // condition
        writeTab();
        out.print("if (!");;
        out.print(inst.varName);
        out.print(") break");
        out.println(";");
        line_num++;

        popNest();
        writeTab();
        out.println("}");
        line_num++;

        writeExit(p);
    }

    public void visit(ExpressionStatement p) throws ParseTreeException {
        if(isSameObject(mutStatement, p)) {
            // instrumentation got everything
            p.getExpression().accept(this);
        }
        else super.visit(p);
    }

    public void visit(ForStatement p) throws ParseTreeException {
        // do nothing special if the mutant is not in the control predicate
        if (!isSameObject(mutStatement, p)) {
            super.visit(p);
            writeExit(p);
            return;
        }

        ExpressionList init = p.getInit();
        TypeName tspec = p.getInitDeclType();
        VariableDeclarator[] vdecls = p.getInitDecls();

        out.println();
        line_num++;

        // initializer
        if (init != null && (!init.isEmpty())) {
            for (int i = 0; i < init.size(); ++i) {
                writeTab();
                init.get(i).accept(this);
                out.println(";");
                line_num++;
            }
        } else if (tspec != null && vdecls != null && vdecls.length != 0) {
            // change the name of variables declared in the initializer
            // to avoid name collision
            itName = new ArrayList<String>();

            for (int i = 0; i < vdecls.length; ++i) {
                if(isSameObject(vdecls[i].getInitializer(), mutExpression)) {
                    // write instrumentation first
                    super.visit(inst.init);
                    for (String str : inst.assertion) writeString(str);
                    super.visit(inst.post);
                }

                writeTab();
                tspec.accept(this);
                out.print(" ");
                itName.add(vdecls[i].getVariable());

                writeNewName(vdecls[i], i);
                out.println(";");
                line_num++;
            }
        }

        writeTab();
        out.println("while (true) {");
        line_num++;
        pushNest();

        // condition
        Expression expr = p.getCondition();
        if(isSameObject(expr, mutExpression)){
            super.visit(inst.init);
            for (String str : inst.assertion) writeString(str);
            super.visit(inst.post);

            writeTab();
            out.print("if (!");;
            out.print(inst.varName);
            out.print(") break");
            out.println(";");
            line_num++;
        } else if (expr != null) {
            writeTab();
            out.print("if (!(");
            expr.accept(this);
            out.println(")) break;");
            line_num++;
        }

        out.println();
        line_num++;

        // main body
        StatementList stmts = p.getStatements();
        if (!stmts.isEmpty()) stmts.accept(this);
        out.println();
        line_num++;

        // the increment part of the control predicate
        ExpressionList incr = p.getIncrement();
        if (incr != null && (!incr.isEmpty())) {
            for (int i = 0; i < incr.size(); ++i) {
                if(isSameObject(incr.get(i), mutExpression)){
                    super.visit(inst.init);
                    for (String str : inst.assertion) writeString(str);
                    super.visit(inst.post);
                } else {
                    writeTab();
                    incr.get(i).accept(this);
                    out.println(";");
                    line_num++;
                }
            }
        }

        popNest();
        writeTab();
        out.println("}");
        line_num++;

        writeExit(p);

        itName = null;
    }

    public void visit(IfStatement p) throws ParseTreeException {
        if(!isSameObject(p, mutStatement)){
            super.visit(p);
            return;
        }

        // the mutant is the conditional statement
        out.println();
        line_num++;
        super.visit(inst.init);
        for (String str : inst.assertion) writeString(str);
        super.visit(inst.post);
        writeExit(p);

        writeTab();
        out.print("if (");;
        out.print(inst.varName);
        out.print(") ");

        /* then part */
        StatementList stmts = p.getStatements();
        writeStatementsBlock(stmts);

        /* else part */
        StatementList elsestmts = p.getElseStatements();
        if (!elsestmts.isEmpty()) {
            out.print(" else ");
            writeStatementsBlock(elsestmts);
        }

        out.println();
        line_num++;
    }

    public void visit(Variable p) throws ParseTreeException {
        String name = p.toString();

        // if the variable is declared in for-loop initialization statement
        // its name must mangled to avoid collision
        if (itName != null){
            for(int i = 0; i < itName.size(); ++i)
                if (itName.get(i).equals(name)) {
                    out.print(InstConfig.varPrefix + "FOR_" + i);
                    return;
                }
        }
        out.print(name);
    }

    public void visit(VariableDeclaration p) throws ParseTreeException{
        // do nothing special if the mutant is not in the declaration
        if (!isSameObject(mutStatement, p)) {
            super.visit(p);
            return;
        }

        out.println();
        line_num++;
        super.visit(inst.init);
        for (String str : inst.assertion) writeString(str);
        super.visit(inst.post);

        writeTab();

        ModifierList modifs = p.getModifiers();
        modifs.accept(this);
        if (!modifs.isEmptyAsRegular()) out.print(" ");

        TypeName typespec = p.getTypeSpecifier();
        typespec.accept(this);

        out.print(" ");

        VariableDeclarator decl = p.getVariableDeclarator();

        String declname = decl.getVariable();
        out.print(declname);

        for (int i = 0; i < decl.getDimension(); ++i) {
            out.print("[]");
        }

        // assign the final value of the instrument
        VariableInitializer varinit = decl.getInitializer();
        if (varinit != null) {
            out.println(" = " + inst.varName + ";");
            line_num++;
        }

        writeExit(p);
        out.println();
        line_num++;
    }

    public void visit(WhileStatement p) throws ParseTreeException {
        // do nothing special if the mutant is not in the control predicate
        if (!isSameObject(mutStatement, p)) {
            super.visit(p);
            writeExit(p);
            return;
        }

        // otherwise, the mutant is the conditional statement of this loop

        writeTab();
        out.println("while (true) {");
        line_num++;
        pushNest();

        // instrumentation
        super.visit(inst.init);
        for (String str : inst.assertion) writeString(str);
        super.visit(inst.post);

        // condition
        writeTab();
        out.print("if (!");;
        out.print(inst.varName);
        out.print(") break");
        out.println(";");
        line_num++;

        out.println();
        line_num++;
        // main body
        StatementList stmts = p.getStatements();
        if (!stmts.isEmpty()) stmts.accept(this);

        popNest();
        writeTab();
        out.println("}");
        line_num++;

        writeExit(p);
    }

    // write a string that denotes a single line of code
    private void writeString(String str) {
        writeTab();

        out.println(str);
        line_num++;
    }

    // defined a variable with a different name
    // only for for-loop
    private void writeNewName(VariableDeclarator p, int index)
            throws ParseTreeException{
        out.print(InstConfig.varPrefix + "FOR_" + index);

        for (int i = 0; i < p.getDimension(); ++i) {
            out.print("[]");
        }

        VariableInitializer varinit = p.getInitializer();
        if (varinit != null) {
            out.print(" = ");
            if (isSameObject(varinit, mutExpression)) out.print(inst.varName);
            else varinit.accept(this);
        }
    }

    // write the exit string if this is the end of block
    private void writeExit(Statement p){
        if(encBlock == null || isSameObject(p, encBlock)) {
            writeString(Instrument.exit);
            out.println();
            line_num++;
        }
    }
}
