package de.variantsync.matching.emf2csv;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.State;
import org.eclipse.uml2.uml.Transition;
import org.eclipse.uml2.uml.UMLPackage;

import com.opencsv.CSVWriter;

/**
 * Utility class for transforming a set of UML models (i.e., variants of each
 * other) in the EMF format into an element-property representation stored as a
 * single CSV file as required by RaQuN.
 * 
 * To date, the converter supports only parts of the UML, namely classdiagrams
 * and statemachines.
 *
 */
public class UML2CSV {

	public static void main(final String[] args) throws Exception {
		System.out.println("Running UML2CSV...");

		// Get arguments and prepare the conversion settings
		if (args.length != 2) {
			System.err.println("Required arguments: ['classdiagram'|'statemachine'] <model-directory>");
			System.exit(0);
		}

		if (!args[0].equals("classdiagram") && !args[0].equals("statemachine")) {
			System.err.println("Required arguments: ['classdiagram'|'statemachine'] <model-directory>");
			System.exit(0);
		}
		final List<EClass> elementTypes = new ArrayList<>();
		if (args[0].equals("classdiagram")) {
			elementTypes.add(UMLPackage.eINSTANCE.getClass_());
		}
		if (args[0].equals("statemachine")) {
			elementTypes.add(UMLPackage.eINSTANCE.getState());
			elementTypes.add(UMLPackage.eINSTANCE.getTransition());
		}

		final File inFolder = new File(args[1]);
		if (!inFolder.exists() || !inFolder.isDirectory()) {
			System.err.println("Directory does not exist: " + inFolder.getAbsolutePath());
			System.exit(0);
		}

		final File outFile = new File("csv-models/" + inFolder.getName() + ".csv");
		outFile.getParentFile().mkdirs();

		System.out.println("Model Type: " + args[0]);
		System.out.println("Model Directory: " + inFolder.getAbsolutePath());

		// Start the actual conversion
		convert(inFolder, outFile, elementTypes);
	}

	private static void convert(final File inFolder, final File outFile, final List<EClass> elementTypes) throws Exception {
		final List<String[]> allElementRecords = new ArrayList<>();

		final File[] modelFiles = inFolder.listFiles();
		for (final File file : modelFiles) {
			System.out.println("Processing " + file.getAbsolutePath());
			final Model model = ModelUtil.loadModel(file.getAbsolutePath());
			String modelId = file.getName();
			modelId = modelId.substring(0, modelId.length() - 4);

			// Elements
			for (final EClass elementType : elementTypes) {
				for (final NamedElement element : ModelUtil.getAllElements(model, elementType)) {
					final String[] elementRecord = new String[4];
					elementRecord[0] = modelId;
					elementRecord[1] = ModelUtil.getXmiId(element);
					elementRecord[2] = element.getName();

					// Properties
					if (elementType == UMLPackage.eINSTANCE.getClass_()) {
						elementRecord[3] = getClassPropertyString((Class) element);
					}
					if (elementType == UMLPackage.eINSTANCE.getState()) {
						elementRecord[3] = getStatePropertyString((State) element);
					}
					if (elementType == UMLPackage.eINSTANCE.getTransition()) {
						elementRecord[3] = getTransitionPropertyString((Transition) element);
					}

					allElementRecords.add(elementRecord);
				}
			}
		}

		writeCSV(allElementRecords, outFile.getAbsolutePath());
	}

	private static String getClassPropertyString(final Class clazz) {
		String propertyString = "";
		final List<Feature> properties = ModelUtil.getAllFeatures(clazz);
		for (final Feature feature : properties) {
			// Semicolon to separate features needed?
			if (!propertyString.isEmpty()) {
				propertyString += ";";
			}
			// String representation of property
			if (feature instanceof Operation) {
				// Special handling of Operations
				final Operation operation = (Operation) feature;
				propertyString += operation.getName();
				if (operation.getOwnedParameters().isEmpty()) {
					propertyString += "_";
				} else {
					for (final Parameter param : operation.getOwnedParameters()) {
						propertyString += "_" + param.getName();
					}
				}
			} else {
				// Normal Attribute
				propertyString += feature.getName();
			}
		}

		return propertyString;
	}

	private static String getStatePropertyString(final State state) {
		String propertyString = "State";

		for (final Transition t : state.getOutgoings()) {
			propertyString += ";out_" + t.getName();
		}
		for (final Transition t : state.getIncomings()) {
			propertyString += ";in_" + t.getName();
		}

		if (state.getEntry() != null) {
			propertyString += ";" + state.getEntry().getName();
		}

		if (state.getDoActivity() != null) {
			propertyString += ";" + state.getDoActivity().getName();
		}

		if (state.getExit() != null) {
			propertyString += ";" + state.getExit().getName();
		}

		return propertyString;
	}

	private static String getTransitionPropertyString(final Transition transition) {
		String propertyString = "Transition";

		propertyString += ";src_" + transition.getSource().getName();
		propertyString += ";tgt_" + transition.getTarget().getName();

		if (transition.getEffect() != null) {
			propertyString += ";" + transition.getEffect().getName();
		}
		if (transition.getGuard() != null) {
			propertyString += ";" + transition.getGuard().getName();
		}

		return propertyString;
	}

	protected static void writeCSV(final List<String[]> stringArray, final String path) throws Exception {
		System.out.println("Writing Output CSV: " + path);
		final CSVWriter writer = new CSVWriter(new FileWriter(path.toString()), ',', Character.MIN_VALUE);
		writer.writeAll(stringArray);
		writer.close();
	}
}
