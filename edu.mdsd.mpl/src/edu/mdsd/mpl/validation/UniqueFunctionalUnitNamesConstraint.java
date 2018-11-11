package edu.mdsd.mpl.validation;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import edu.mdsd.mpl.FunctionalUnit;
import edu.mdsd.mpl.MPLModel;

public class UniqueFunctionalUnitNamesConstraint extends AbstractModelConstraint {

	public UniqueFunctionalUnitNamesConstraint() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public IStatus validate(IValidationContext context) {
		FunctionalUnit unit = (FunctionalUnit) context.getTarget();
		MPLModel model = (MPLModel) unit.eContainer();
		
		String unitName = unit.getName();
		
		List<FunctionalUnit> units = model.getOperations().stream().map(operation -> (FunctionalUnit) operation).collect(Collectors.toList());
		if(model.getProgram() != null) {
			units.add(model.getProgram());
		}
		
		for(FunctionalUnit otherUnit : units) {
			if(otherUnit != unit) {
				
				if(otherUnit.getName() == unitName) {
					return context.createFailureStatus(unitName);
				}
			}
		}
		
		return context.createSuccessStatus();
	}

}
