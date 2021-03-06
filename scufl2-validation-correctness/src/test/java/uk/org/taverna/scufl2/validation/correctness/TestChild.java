/**
 * 
 */
package uk.org.taverna.scufl2.validation.correctness;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Set;

import org.junit.Test;

import uk.org.taverna.scufl2.api.common.Child;
import uk.org.taverna.scufl2.api.common.Visitor;
import uk.org.taverna.scufl2.api.common.WorkflowBean;
import uk.org.taverna.scufl2.api.container.WorkflowBundle;
import uk.org.taverna.scufl2.api.core.Workflow;
import uk.org.taverna.scufl2.api.port.InputActivityPort;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NegativeValueProblem;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.NullFieldProblem;
import uk.org.taverna.scufl2.validation.correctness.ReportCorrectnessValidationListener.WrongParentProblem;

/**
 * @author alanrw
 *
 */
public class TestChild {



	/**
	 * Test method for {@link uk.org.taverna.scufl2.validation.correctness.CorrectnessVisitor#visitChild(uk.org.taverna.scufl2.api.common.Child)}.
	 */
	@Test
	public void testCorrectnessOfMissingParent() {
		Workflow w = new Workflow();
		w.setName("fred");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, false, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size()); // only done when completeness check
	}
	
	@Test
	public void testCompletenessOfMissingParent() {
		Workflow w = new Workflow();
		w.setName("fred");
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(1, nullFieldProblems.size()); // parent
		boolean parentProblem = false;
		for (NullFieldProblem nlp : nullFieldProblems) {
			if (nlp.getBean().equals(w) && nlp.getFieldName().equals("parent")) {
				parentProblem = true;
			}
		}
		assertTrue(parentProblem);
	}
	
	@Test
	public void testCompletenessOfSpecifiedParent() {
		Workflow w = new Workflow();
		w.setName("fred");
		WorkflowBundle wb = new WorkflowBundle();
		w.setParent(wb);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(w, true, rcvl);
		
		Set<NullFieldProblem> nullFieldProblems = rcvl.getNullFieldProblems();
		assertEquals(0, nullFieldProblems.size());
	}
	
	@Test
	public void testValidParent() {
		WorkflowBundle parent = new WorkflowBundle();
		Workflow fw = new Workflow();
		fw.setParent(parent);
				
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(parent, false, rcvl);
		Set<WrongParentProblem> wrongParentProblems = rcvl.getWrongParentProblems();
		assertEquals(Collections.EMPTY_SET, wrongParentProblems);
	}
	
	@Test
	public void testInvalidParent() {

		WorkflowBundle parent = new WorkflowBundle();
		DummyWorkflow fw = new DummyWorkflow(parent);
		
		CorrectnessValidator cv = new CorrectnessValidator();
		ReportCorrectnessValidationListener rcvl = new ReportCorrectnessValidationListener();
		
		cv.checkCorrectness(parent, false, rcvl);
		Set<WrongParentProblem> wrongParentProblems = rcvl.getWrongParentProblems();
		assertEquals(1, wrongParentProblems.size());
		boolean parentProblem = false;
		for (WrongParentProblem p : wrongParentProblems) {
			if (p.getBean().equals(fw)) {
				parentProblem = true;
			}
		}
		assertTrue(parentProblem);
	}



}
