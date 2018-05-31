package analysis;

import java.io.Serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.util.*;

import ecproj.krgen.ClinicalTrialInfo;
import ecproj.krgen.Criterion;
import ecproj.krgen.Dimension;
import ecproj.krgen.Intervention;
import ecproj.krgen.Group;
import ecproj.krgen.UMLSConcept;
import ecproj.krgen.Pattern;
import ecproj.krgen.UMLSConceptList;
import ecproj.krgen.Conflict;


public class ConflictAnalysis {

	//label ArrayList for counting
	public static ArrayList<LabelCount> labelCount = new ArrayList<LabelCount>();
	public static int criteriaCount = 0;
	
	//labels to exclude
	public static ArrayList<String> labelsExcl = new ArrayList<String>();
	
	//for counting
	public static ArrayList<String> patternTypes = new ArrayList<String>();	//used to store pattern types
	public static ArrayList<String> cuiList = new ArrayList<String>();
	public static ArrayList<String> semanticTypeList = new ArrayList<String>();
	

	public static void main(String[] args) throws FileNotFoundException {
	
		ArrayList<ClinicalTrialInfo> trials = readSerializedList();
		
		for(int i=0; i<trials.size(); i++){
			if(trials.get(i).getNctId().equals("NCT01991652") || trials.get(i).getNctId().equals("NCT02405338")){
				trials.remove(i);
			}
		}//removing 2 problematic trials
		
		System.out.println("The number of trials analyzed is " + trials.size());
		
		PrintWriter output = new PrintWriter("Conflicts.txt");
				
		int[][] conflictMatrix = new int[trials.size()][trials.size()];	//conflict score matrix
		
		//list of categories (criterionType) to exclude from conflicts
		labelsExcl.add("ecClassifier.AGE_CRIT_EC");
		labelsExcl.add("ecClassifier.DIAG_CRIT_EC");
		labelsExcl.add("ecClassifier.DIS_CRIT_EC");
		labelsExcl.add("ecClassifier.HISTDIAG_CRIT_EC");
		labelsExcl.add("ecClassifier.INFCON_CRIT_EC");
		labelsExcl.add("ecClassifier.LIFE_CRIT_EC");
		labelsExcl.add("ecClassifier.MEAS_CRIT_EC");
		labelsExcl.add("ecClassifier.MENO_CRIT_EC");
		labelsExcl.add("ecClassifier.PATCHAR_CRIT_EC");
		labelsExcl.add("ecClassifier.PROG_CRIT_EC");
		labelsExcl.add("ecClassifier.SEX_CRIT_EC");
		labelsExcl.add("ecClassifier.WGT_CRIT_EC");
		
		labelCount.add(new LabelCount("ecClassifier.AGE_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.BIO_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.CARD_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.CHEMO_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.DIAG_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.DIS_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.END_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.EXC_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.GAST_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HEM_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HEMAT_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HEP_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HEPFUNC_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HIST_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HISTDIAG_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.HORRECPT_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.IMMUN_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.INC_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.INFCON_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.LIFE_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.MEAS_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.MENO_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.NEURO_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.OPT_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.OTH_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.PATCHAR_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.PERF_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.PRIOR_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.PROG_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.PSYCH_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.PULM_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.RADIO_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.REN_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.RENFUNC_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.REC_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.SEX_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.SURG_CRIT_EC")); 
		labelCount.add(new LabelCount("ecClassifier.WGT_CRIT_EC"));
		
		//computing conflictSum values for each trial
		for(int i=0; i<trials.size(); i++)
		{
			int conflictSum = 0;
			output.println("**************************************");
			output.println("Trial: " + trials.get(i).getNctId());
			output.println("Conflicts: ");
			
			for(int j=0; j<trials.size(); j++)
			{
				if(j != i)
				{
					int conflictScore;
					conflictScore = computeConflictScore(trials.get(j), trials.get(i), output);
					conflictSum += conflictScore;
					
					conflictMatrix[j][i] = conflictScore;
					
				}
				
			}
			trials.get(i).setConflictSum(conflictSum);
			output.println("Total conflicts for trial " + trials.get(i).getNctId() + " : " + conflictSum);
			output.println("**************************************\n\n");
		}
		
		output.close();
		
		//getting counts for pattern types and writing to file
		Set<String> uniquePatternTypes = new HashSet<String>(patternTypes); //unique patterns
		
		PrintWriter patternTypeFile = new PrintWriter("PatternTypes.txt");
		
		patternTypeFile.println("PatternType \t Frequency in Conflicts");
		
		for(String pattern: uniquePatternTypes){
			patternTypeFile.println(pattern + "\t" + Collections.frequency(patternTypes, pattern));
		}
		
		patternTypeFile.close();
		
		//getting counts for cuis and writing to file
		Set<String> uniqueCuis = new HashSet<String>(cuiList); //unique cuis
		
		PrintWriter cuiListFile = new PrintWriter("CuiList.txt");
		
		cuiListFile.println("CUI \t Frequency in Conflicts");
		
		for(String c: uniqueCuis){
			cuiListFile.println(c + "\t" + Collections.frequency(cuiList, c));
		}
		
		cuiListFile.close();
		
		//getting counts for semantic types and writing to file
		Set<String> uniqueSemanticTypes = new HashSet<String>(semanticTypeList); //unique semantic types
		
		PrintWriter semanticTypeFile = new PrintWriter("SemanticTypeList.txt");
		
		semanticTypeFile.println("SemanticType \t Frequency in Conflicts");
		
		for(String s: uniqueSemanticTypes){
			semanticTypeFile.println(s + "\t" + Collections.frequency(semanticTypeList, s));
		}
		
		semanticTypeFile.close();
		
		//print conflict score matrix to file
		PrintWriter matrixFile = new PrintWriter("ConflictScoreMatrix.txt");
		
		//print heading
		for(ClinicalTrialInfo t: trials){
			matrixFile.print(";" + t.getNctId());
		}
		matrixFile.println();
		
		
		for(int i=0; i<conflictMatrix.length; i++){
			
			matrixFile.print(trials.get(i).getNctId());
			
			for(int j=0; j<conflictMatrix[i].length; j++){
				
				matrixFile.print(";" + conflictMatrix[i][j]);
				
			}
			matrixFile.println();
		}
		
		matrixFile.close();
		
		
		//display trials, sorted by number of conflicts
		Collections.sort(trials); 
		
		System.out.println("Trial id \t conflictSum");
		
		for(ClinicalTrialInfo t: trials){
			System.out.println(t.getNctId() + "\t" + t.getConflictSum());
		}
		
		for(int i=0; i<trials.size(); i++)
		{
			categoryCounts(trials.get(i));
		}
		
		for(LabelCount c: labelCount){
			System.out.println(c.getCriterionType() + " " + c.getCount());
		}
		
		System.out.println("Criteria count: " + criteriaCount);
		
	}//end of main
	
	public static int computeConflictScore(ClinicalTrialInfo A, ClinicalTrialInfo B, PrintWriter output) throws FileNotFoundException{
		
		int conflictScore = 0;
		
    	//iterate through Criterion of trial B
		Iterator<Criterion> citer = B.getCriteriaIterator();

		while (citer.hasNext())
		{	
			Criterion crit = citer.next();
			
			//skip criterion if it's category is excluded
			if(labelsExcl.contains(crit.getCriterionType())){
				
				continue;
			}
			
			//list of matched CUIs in a criterion, used to avoid counting duplicate CUIs in a criterion
			ArrayList<String> cuis = new ArrayList<String>();
			//lists of matched covered text, used to avoid duplicate conflicts for the same covered text in a criterion
			ArrayList<String> coveredTextInterventions = new ArrayList<String>();
			ArrayList<String> coveredTextCriteria = new ArrayList<String>();
			
			//iterate through Interventions of trial A
			Iterator<Intervention> interIter = A.getInteventionsIterator();
		
			while (interIter.hasNext())
			{
				Intervention intervention = interIter.next();
		
				//iterate through concepts for Interventions of trial A
				Iterator<UMLSConcept> conceptIterA = intervention.getUMLSIterator();

				while (conceptIterA.hasNext())
				{
					UMLSConcept conceptA = conceptIterA.next();
		
					//iterate through Patterns of Criteria in trial B
					Iterator<Pattern> matchIter = crit.getPatternIterator();

					while (matchIter.hasNext())
					{
						Pattern pat = matchIter.next();
						
						if(pat.getRuleInfo().useForConflicts())
						{
							Group g1 = pat.getGroup1();
							
							Iterator<UMLSConcept> conceptIterB;
							
							if(g1 != null)
							{
								if(pat.getRuleInfo().conditionIsExcluded(0))
								{
									//iterate through concepts for group1
									conceptIterB = g1.getUMLSIterator();
									while (conceptIterB.hasNext())
								    {
								    	UMLSConcept conceptB = conceptIterB.next();
								    	
								    	System.out.println("got here 2" + pat.getTypeName());
								    	
								    	
								    	if(conceptA.getCui().equals(conceptB.getCui()) && !cuis.contains(conceptA.getCui()) && !coveredTextInterventions.contains(conceptA.getCoveredText()) && !coveredTextCriteria.contains(conceptB.getCoveredText()) && !conceptA.getCui().equals("C0087111") && !conceptA.getCui().equals("C1363945") && !conceptA.getCui().equals("C0332287") && !conceptA.getCui().equals("C0039798"))
								    	{
								    			conflictScore++;
								    			output.println(A.getNctId() + " " + conceptB.getCui());
								    			output.println("Intervention covered text: " + conceptA.getCoveredText());
								    			output.println("Criteria covered text: "+  conceptB.getCoveredText()); 
								    			output.println("Criterion: " + crit.getText());
								    			output.println("Pattern Type: " + pat.getTypeName() + "\n");
								    			
								    			Conflict c = new Conflict();
								    			c.setNctId(A.getNctId());
								    			c.setCui(conceptB.getCui());
								    			c.setInterventionCoveredText(conceptA.getCoveredText());
								    			c.setCriteriaCoveredText(conceptB.getCoveredText());
								    			c.setCriterion(crit.getText());
								    			c.setPatternTypeName(pat.getTypeName());
								    			B.addConflict(c);
								    			
								    			cuis.add(conceptA.getCui());
								    			coveredTextInterventions.add(conceptA.getCoveredText());
								    			coveredTextCriteria.add(conceptB.getCoveredText());
								    			
								    			//for counting
								    			patternTypes.add(pat.getTypeName());
								    			cuiList.add(conceptA.getCui());
								    			semanticTypeList.addAll(conceptA.getSemanticTypes());
								    		
								    	}
								    }
								}
							}
							
							Group g2 = pat.getGroup2();
							
							if(g2 != null)
							{
								if(pat.getRuleInfo().conditionIsExcluded(1))
								{
									//iterate through concepts for group2
									conceptIterB = g2.getUMLSIterator();
		
									while (conceptIterB.hasNext())
								    {	
								    	UMLSConcept conceptB = conceptIterB.next();
								    
								    //	System.out.println("got here " + pat.getTypeName());
								    	
								    	if(conceptA.getCui().equals(conceptB.getCui()) && !cuis.contains(conceptA.getCui()) && !coveredTextInterventions.contains(conceptA.getCoveredText()) && !coveredTextCriteria.contains(conceptB.getCoveredText()) && !conceptA.getCui().equals("C0087111") && !conceptA.getCui().equals("C1363945") && !conceptA.getCui().equals("C0332287") && !conceptA.getCui().equals("C0039798"))
								    	{	
								    			conflictScore++;
								    			output.println(A.getNctId() + " " + conceptB.getCui());
								    			output.println("Intervention covered text: " + conceptA.getCoveredText());
								    			output.println("Criteria covered text: "+  conceptB.getCoveredText()); 
								    			output.println("Criterion: " + crit.getText());
								    			output.println("Pattern Type: " + pat.getTypeName() + "\n");
								    			
								    			Conflict c = new Conflict();
								    			c.setNctId(A.getNctId());
								    			c.setCui(conceptB.getCui());
								    			c.setInterventionCoveredText(conceptA.getCoveredText());
								    			c.setCriteriaCoveredText(conceptB.getCoveredText());
								    			c.setCriterion(crit.getText());
								    			c.setPatternTypeName(pat.getTypeName());
								    			B.addConflict(c);
								    			
								    			cuis.add(conceptA.getCui());
								    			coveredTextInterventions.add(conceptA.getCoveredText());
								    			coveredTextCriteria.add(conceptB.getCoveredText());
								    			
								    			//for counting
								    			patternTypes.add(pat.getTypeName());
								    			cuiList.add(conceptA.getCui());
								    			semanticTypeList.addAll(conceptA.getSemanticTypes());
								    		
								    	}
								    }
								}
							}
							
							Group g3 = pat.getGroup3();
							
							if(g3 != null)
							{
								if(pat.getRuleInfo().conditionIsExcluded(2))
								{
									//iterate through concepts for group3
									conceptIterB = g3.getUMLSIterator();
									while (conceptIterB.hasNext())
								    {
								    	UMLSConcept conceptB = conceptIterB.next();
								    	
								    	
								    //	System.out.println("got here " + pat.getTypeName());
								    	if(conceptA.getCui().equals(conceptB.getCui()) && !cuis.contains(conceptA.getCui()) && !coveredTextInterventions.contains(conceptA.getCoveredText()) && !coveredTextCriteria.contains(conceptB.getCoveredText()) && !conceptA.getCui().equals("C0087111") && !conceptA.getCui().equals("C1363945") && !conceptA.getCui().equals("C0332287") && !conceptA.getCui().equals("C0039798"))
								    	{	
		
								    			conflictScore++;
								    			output.println(A.getNctId() + " " + conceptB.getCui());
								    			output.println("Intervention covered text: " + conceptA.getCoveredText());
								    			output.println("Criteria covered text: "+  conceptB.getCoveredText()); 
								    			output.println("Criterion: " + crit.getText());
								    			output.println("Pattern Type: " + pat.getTypeName() + "\n");
								    			
								    			Conflict c = new Conflict();
								    			c.setNctId(A.getNctId());
								    			c.setCui(conceptB.getCui());
								    			c.setInterventionCoveredText(conceptA.getCoveredText());
								    			c.setCriteriaCoveredText(conceptB.getCoveredText());
								    			c.setCriterion(crit.getText());
								    			c.setPatternTypeName(pat.getTypeName());
								    			B.addConflict(c);
								    			
								    			cuis.add(conceptA.getCui());
								    			coveredTextInterventions.add(conceptA.getCoveredText());
								    			coveredTextCriteria.add(conceptB.getCoveredText());
								    			
								    			//for counting
								    			patternTypes.add(pat.getTypeName());
								    			cuiList.add(conceptA.getCui());
								    			semanticTypeList.addAll(conceptA.getSemanticTypes());
								 
								    	}
								    }
								}
							}
						}
					}
				}
		    }
		   }

		return conflictScore;
		
	}
	
	public static ArrayList<ClinicalTrialInfo> readSerializedList(){
		ArrayList<ClinicalTrialInfo> inputTrials = null;
		
		FileInputStream fin = null;
		ObjectInputStream ois = null;

		try {

			fin = new FileInputStream("/home/ctproj/GitRepos/krgenRepo/krgen/trials.txt");
			ois = new ObjectInputStream(fin);
			inputTrials = (ArrayList<ClinicalTrialInfo>)ois.readObject();
			
//			for (ClinicalTrialInfo trial : inputTrials)
//				printTrial(trial);
			System.out.println("The number of trials input is " + inputTrials.size());
		} catch (Exception ex) {
			ex.printStackTrace();

	}
		return inputTrials;
	}
	
	private static void printTrial(ClinicalTrialInfo trial) {
		System.out.println();
		System.out.println("*********Clinical Trial: " + trial.getNctId());
		System.out.println("List of interventions:");
		Iterator<Intervention> interIter = trial.getInteventionsIterator();
		int internum = 1;
		while (interIter.hasNext())
		{
			Intervention intervention = interIter.next();
			System.out.println("Intervention " + internum++);
			System.out.println("Intervention Covered Text = " + intervention.getText());
			System.out.println("Intervention name: " + intervention.getName());
			System.out.println("Intervention type: " + intervention.getType());
		    Iterator<UMLSConcept> conceptIter = intervention.getUMLSIterator();
		    while (conceptIter.hasNext())
		    {
		    	UMLSConcept concept = conceptIter.next();
		    	System.out.println("UMLS concept is " + concept);
		    }
		}
		System.out.println("List of Criteria:");
		Iterator<Criterion> citer = trial.getCriteriaIterator();
		int critnum = 1;
		
		while (citer.hasNext())
		{
			Criterion crit = citer.next();
			System.out.println("Criteria " + critnum++);
			System.out.println("Criterion Text = " + crit.getText());
			System.out.println("Pattern matches are ");
			Iterator<Pattern> matchIter = crit.getPatternIterator();
			while (matchIter.hasNext())
			{
				Pattern pat = matchIter.next();
				System.out.println("Pattern name: " + pat.getTypeName());
				System.out.println("Pattern Text: " + pat.getPattern());
				System.out.println("Group1 contains " + pat.getGroup1());
				System.out.println("Group2 contains " + pat.getGroup2());
				System.out.println("Group3 contains " + pat.getGroup3());
				System.out.println("Rule info: " + pat.getRuleInfo().toString());
				//Iterator<Dimension> dimIter = pat.getDimensionIterator();
				//out.println("The dimensions are ");
			//	while (dimIter.hasNext())
				//{
				//	Dimension dim = dimIter.next();
			//		out.println(dim + " ");
			//	}
			}
		}
		
	}
	
	public static void categoryCounts(ClinicalTrialInfo trial){
		
		Iterator<Criterion> citer = trial.getCriteriaIterator();

		while (citer.hasNext())
		{	
			Criterion crit = citer.next();
			
			criteriaCount++;
			System.out.println("Type: " + crit.getCriterionType());
			
			for(LabelCount c: labelCount){
				if(c.getCriterionType().equals(crit.getCriterionType()))
					c.updateCount();
			}
		}
	}
	

}
