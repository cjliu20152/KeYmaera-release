package visualdebugger.astops;

import java.util.*;

import org.eclipse.jdt.core.dom.*;

public class LocalVariableDetector extends ASTVisitor {
    Set<SimpleName> localVariables = new HashSet<SimpleName>();
    private Expression expr;

    public LocalVariableDetector(Expression e){
        this.expr = e;
    }    
    
    public boolean visit(VariableDeclarationStatement node) {
        
        VariableDeclarationFragment fragment = (VariableDeclarationFragment) node.fragments().get(0);
        Expression initializer = fragment.getInitializer();
        String e = initializer.toString();
        if(e.equals(expr.toString())){
            localVariables.addAll(Util.getOperands(initializer));
        }
        return false;
    }

    
    public Set<SimpleName> getLocalVariables() {
        return localVariables;
    }

    /**
     * Starts the process.
     *
     * @param unit
     *            the AST root node. Bindings have to have been resolved.
     */
    public void process(CompilationUnit unit) {
        unit.accept(this);
    }
}