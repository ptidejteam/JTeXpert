package csbst.generators;

import java.util.List;
import java.util.Random;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.Statement;

import csbst.testing.JTE;

public class CopyGenerator extends AbsractGenerator{
	//Gene geneSource;
	
	public CopyGenerator() {
		super(null,null);
		//geneSource=gene;
	}

	@Override
	public void generateRandom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mutate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Statement> getStatements(AST ast, String varName, String pName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSameFamillyAs(AbsractGenerator gene) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}


}

