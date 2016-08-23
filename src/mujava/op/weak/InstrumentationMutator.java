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

import mujava.op.util.MethodLevelMutator;
import openjava.mop.*;
import openjava.ptree.*;

/**
 * <p>Keeps track of where the mutant is</p>
 * @author Haoyuan Sun
 * @version 0.1a
 */

public abstract class InstrumentationMutator extends MethodLevelMutator {
    public InstrumentationMutator(FileEnvironment file_env, CompilationUnit comp_unit) {
        super(file_env, comp_unit);
    }

    // indicate the outter most block which encloses the mutant
    protected Statement encBlock;

    // indicate which block was mutated
    protected Statement mutBlock;

    // indicate which statement was mutated
    protected Statement mutStatement;

    // indicate which expression was mutated
    protected Expression mutExpression;

    public void visit(DoWhileStatement p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        if(encBlock == null) encBlock = p;

        mutBlock = p;
        mutStatement = p;

        p.getExpression().accept(this);

        mutStatement = null;

        p.getStatements().accept(this);

        mutBlock = null;

        if(encBlock.getObjectID() == p.getObjectID()) encBlock = null;

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }

    public void visit(ExpressionStatement p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        mutStatement = p;

        p.getExpression().accept(this);

        mutStatement = null;

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }

    public void visit(ForStatement p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        if(encBlock == null) encBlock = p;

        mutBlock = p;

        ExpressionList init = p.getInit();
        TypeName tspec = p.getInitDeclType();
        VariableDeclarator[] vdecls = p.getInitDecls();
        String identifier = p.getIdentifier();
        // no mutant in a for-each loop
        if(identifier == null){
            mutStatement = p;

            if (init != null && (!init.isEmpty()))
                init.accept(this);
            else if (tspec != null && vdecls != null && vdecls.length != 0)
                for(VariableDeclarator decl : vdecls) decl.accept(this);

            p.getCondition().accept(this);

            p.getIncrement().accept(this);

            mutStatement = null;
        }

        p.getStatements().accept(this);

        mutBlock = null;

        if(encBlock.getObjectID() == p.getObjectID()) encBlock = null;

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }

    public void visit(IfStatement p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        mutStatement = p;

        p.getExpression().accept(this);

        mutStatement = null;

        p.getStatements().accept(this);

        p.getElseStatements().accept(this);

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }

    public void visit(ReturnStatement p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        mutStatement = p;

        p.getExpression().accept(this);

        mutStatement = null;

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }

    public void visit(VariableDeclaration p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        mutStatement = p;

        p.getInitializer().accept(this);

        mutStatement = null;

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }

    public void visit(WhileStatement p) throws ParseTreeException {
        Statement newp = this.evaluateDown(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }

        if(encBlock == null) encBlock = p;

        mutBlock = p;
        mutStatement = p;

        p.getExpression().accept(this);

        mutStatement = null;

        p.getStatements().accept(this);

        mutBlock = null;

        if(encBlock.getObjectID() == p.getObjectID()) encBlock = null;

        newp = this.evaluateUp(p);
        if (newp != p) {
            p.replace(newp);
            return;
        }
    }
}
