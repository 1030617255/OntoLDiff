package inferencing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import checkexistence.EChecker;
import checkfrequency.FChecker;
import concepts.AtomicConcept;
import concepts.BottomConcept;
import concepts.TopConcept;
import roles.AtomicRole;
import roles.BottomRole;
import roles.TopRole;
import connectives.And;
import connectives.Exists;
import connectives.Forall;
import connectives.Negation;
import connectives.Or;
import formula.Formula;
import individual.Individual;
import preprocessing.PreProcessor;

public class Inferencer {
	
	//The new inferencer class

	public static Map<Formula, AtomicConcept> definer_map = new HashMap<>();
	public static Set<AtomicConcept> definer_set = new HashSet<>();

	public Inferencer() {

	}

	private boolean AckermannPositive;
	private boolean AckermannNegative;

	public boolean getAckermannPositive() {
		return AckermannPositive;
	}

	public void setAckermannPositive(boolean AckermannPositive) {
		this.AckermannPositive = AckermannPositive;
	}

	public boolean getAckermannNegative() {
		return AckermannNegative;
	}

	public void setAckermannNegative(boolean AckermannNegative) {
		this.AckermannNegative = AckermannNegative;
	}

	public List<Formula> introduceDefiners(AtomicConcept concept, List<Formula> input_list) throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			output_list.addAll(introduceDefiners(concept, formula));
		}

		return output_list;
	}
	
	/*
	private List<Formula> introduceDefiners(AtomicConcept concept, Formula formula) throws CloneNotSupportedException {
		
		EChecker ec = new EChecker();
		FChecker fc = new FChecker();
		PreProcessor pp = new PreProcessor();
		
		List<Formula> output_list = new ArrayList<>();
		
		if (ec.isPresent(concept, formula)) {
			
			if (formula instanceof Exists) {
				Formula filler = formula.getSubFormulas().get(0);
				
				if (filler.equals(concept) || filler.equals(new Negation(concept))) {
					output_list.add(formula);

				} else {
					Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
					
					if (definer_map.get(filler) != null) {
						AtomicConcept definer = definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					
					} else if (definer_map.get(filler_negated) != null) {

						Formula definer_negated = new Negation(definer_map.get(filler_negated));
						formula.getSubFormulas().set(1, definer_negated);
						output_list.add(formula);
						
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(definer_map.get(filler_negated));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
						}
						
					}
					
				}
				
				
				
			} else if (formula instanceof Forall) {
				
				
			}
			
			
		}
		
		
	}*/
	
	private List<Formula> introduceDefiners(AtomicConcept concept, Formula formula) throws CloneNotSupportedException {

		EChecker ec = new EChecker();
		FChecker fc = new FChecker();
		PreProcessor pp = new PreProcessor();
		
		List<Formula> output_list = new ArrayList<>();

		if (ec.isPresent(concept, formula)) {

			if (formula instanceof Exists || formula instanceof Forall) {
				Formula filler = formula.getSubFormulas().get(1);

				if (filler.equals(concept) || filler.equals(new Negation(concept))) {
					output_list.add(formula);

				} else {
					
					Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
					
					if (definer_map.get(filler) != null) {
						AtomicConcept definer = definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					
					} else if (definer_map.get(filler_negated) != null) {

						Formula definer_negated = new Negation(definer_map.get(filler_negated));
						formula.getSubFormulas().set(1, definer_negated);
						output_list.add(formula);
						
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(definer_map.get(filler_negated));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
						}
						
					} else {	

						AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(new Negation(definer));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
						}
						
					}
					
/*					
					AtomicConcept definer = null;
					if (definer_map.get(filler) == null) {
						definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler)));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(new Negation(definer));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
						}

					} else {
						definer = definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					}*/
				}

			} else if (formula instanceof Or) {
								
				List<Formula> disjuncts = formula.getSubFormulas();
				
				if (fc.positive(concept, formula) + fc.negative(concept, formula) == 1) {					
					if (disjuncts.contains(concept) || disjuncts.contains(new Negation(concept))) {
						output_list.add(formula);

					} else {
						
						for (Formula disjunct : disjuncts) {
							if (ec.isPresent(concept, disjunct)) {
								Formula filler = disjunct.getSubFormulas().get(1);

								if (filler.equals(concept) || filler.equals(new Negation(concept))) {
									output_list.add(formula);
									break;

								} else {
									
									Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
									Set<Entry<Formula, AtomicConcept>> entry_set = definer_map.entrySet();
									for (Entry<Formula, AtomicConcept> entry : entry_set) {
										System.out.println("key = " + entry.getKey() + ": = " + entry.getValue());
									}
									if (definer_map.get(filler) != null) {
										AtomicConcept definer = definer_map.get(filler);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									
									} else if (definer_map.get(filler_negated) != null) {
										Formula definer_negated = new Negation(definer_map.get(filler_negated));
										disjunct.getSubFormulas().set(1, definer_negated);
										output_list.add(formula);
										
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(definer_map.get(filler_negated));
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										}
										
										break;
										
									} else {	

										AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										definer_map.put(filler, definer);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(new Negation(definer));
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										}
										break;
									}
											
							/*		
									AtomicConcept definer = null;
									if (definer_map.get(filler) == null) {
										definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
										AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
										definer_set.add(definer);
										definer_map.put(filler, definer);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler)));
										for (Formula conjunct : conjunct_list) {
											List<Formula> disjunct_list = new ArrayList<>();
											disjunct_list.add(new Negation(definer));
											if (conjunct instanceof Or) {
												disjunct_list.addAll(conjunct.getSubFormulas());
											} else {
												disjunct_list.add(conjunct);	
											}
											output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
										}
										break;

									} else {
										definer = definer_map.get(filler);
										disjunct.getSubFormulas().set(1, definer);
										output_list.add(formula);
										break;
									}*/
								}
							}
						}
					}

					// pivot occurs >=2
				} else {

					for (int i = 0; i < disjuncts.size(); i++) {						
						Formula disjunct = disjuncts.get(i);
						
						if (ec.isPresent(concept, disjunct)
								&& (disjunct instanceof Exists || disjunct instanceof Forall)) {
							if ((fc.positive(concept, formula) + fc.negative(concept, formula))
									- (fc.positive(concept, disjunct) + fc.negative(concept, disjunct)) > 0) {
								
								Formula disjunct_negated = pp.getSimplifiedForm(new Negation(disjunct.clone()));
								
								if (definer_map.get(disjunct) != null) {
									AtomicConcept definer = definer_map.get(disjunct);
									disjuncts.set(i, definer);
									output_list.addAll(introduceDefiners(concept, formula));
									break;
								
								} else if (definer_map.get(disjunct_negated) != null) {
									Formula definer_negated = new Negation(definer_map.get(disjunct_negated));
									disjuncts.set(i, definer_negated);
									List<Formula> disjunct_list = new ArrayList<>();
									disjunct_list.add(definer_map.get(disjunct_negated));
									disjunct_list.add(disjunct);
									output_list.addAll(introduceDefiners(concept, formula));
									output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
									break;
									
								} else {	
									AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(disjunct, definer);
									disjuncts.set(i, definer);
									List<Formula> disjunct_list = new ArrayList<>();
									disjunct_list.add(new Negation(definer));
									disjunct_list.add(disjunct);
									output_list.addAll(introduceDefiners(concept, formula));
									output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
									break;
								}

								/*
								AtomicConcept definer = null;
								if (definer_map.get(disjunct) == null) {
									definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(disjunct, definer);
									disjuncts.set(i, definer);
									List<Formula> disjunct_list = new ArrayList<>();
									disjunct_list.add(new Negation(definer));
									disjunct_list.add(disjunct);
									output_list.addAll(introduceDefiners(concept, formula));
									output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
									break;

								} else {
									definer = definer_map.get(disjunct);
									disjuncts.set(i, definer);
									output_list.addAll(introduceDefiners(concept, formula));
									break;
								} */

							} else {
								
								Formula filler = disjunct.getSubFormulas().get(1);
								Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
								
								if (definer_map.get(filler) != null) {
									AtomicConcept definer = definer_map.get(filler);
									disjunct.getSubFormulas().set(1, definer);
									output_list.add(formula);
									break;
								
								} else if (definer_map.get(filler_negated) != null) {
									Formula definer_negated = new Negation(definer_map.get(filler_negated));
									disjunct.getSubFormulas().set(1, definer_negated);
									output_list.add(formula);
									
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(definer_map.get(filler_negated));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
									}
									
									break;
									
								} else {	
									AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(filler, definer);
									disjunct.getSubFormulas().set(1, definer);
									output_list.add(formula);
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(new Negation(definer));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
									}
									break;
								}
								
								
								
                                /*
								Formula filler = disjunct.getSubFormulas().get(1);

								AtomicConcept definer = null;
								if (definer_map.get(filler) == null) {
									definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(filler, definer);
									disjuncts.get(i).getSubFormulas().set(1, definer);
									output_list.add(formula);
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler)));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(new Negation(definer));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(concept, new Or(disjunct_list)));
									}
									break;

								} else {
									definer = definer_map.get(filler);
									disjuncts.get(i).getSubFormulas().set(1, definer);
									output_list.add(formula);
									break;
								}*/
							}
						}
					}

				}
				// formula not instanceof Exists, Forall, Or
			} else {
				output_list.add(formula);
			}
			// concept not present in formula
		} else {
			output_list.add(formula);
		}

		return output_list;
	}

	public List<Formula> introduceDefiners(AtomicRole role, List<Formula> input_list) throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			output_list.addAll(introduceDefiners(role, formula));
		}

		return output_list;
	}
	
	private List<Formula> introduceDefiners(AtomicRole role, Formula formula) throws CloneNotSupportedException {
		
		EChecker ec = new EChecker();
		FChecker fc = new FChecker();
		PreProcessor pp = new PreProcessor();

		List<Formula> output_list = new ArrayList<>();

		if (ec.isPresent(role, formula)) {
			
			if (formula instanceof Exists || formula instanceof Forall) {
				Formula filler = formula.getSubFormulas().get(1);
				
				if (ec.isPresent(role, filler)) {				
					Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
					
					if (definer_map.get(filler) != null) {
						AtomicConcept definer = definer_map.get(filler);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
					
					} else if (definer_map.get(filler_negated) != null) {
						Formula definer_negated = new Negation(definer_map.get(filler_negated));
						formula.getSubFormulas().set(1, definer_negated);
						output_list.add(formula);
						
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(definer_map.get(filler_negated));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
						}
											
					} else {					
						AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
						AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
						definer_set.add(definer);
						definer_map.put(filler, definer);
						formula.getSubFormulas().set(1, definer);
						output_list.add(formula);
						List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
						for (Formula conjunct : conjunct_list) {
							List<Formula> disjunct_list = new ArrayList<>();
							disjunct_list.add(new Negation(definer));
							if (conjunct instanceof Or) {
								disjunct_list.addAll(conjunct.getSubFormulas());
							} else {
								disjunct_list.add(conjunct);	
							}
							output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
						}										
					}
										
				} else {
					output_list.add(formula);
				}

			} else if (formula instanceof Or) {
				List<Formula> disjuncts = formula.getSubFormulas();
				
				if (fc.positive(role, formula) + fc.negative(role, formula) == 1) {
					for (Formula disjunct : disjuncts) {
						if (ec.isPresent(role, disjunct)) {
							Formula filler = disjunct.getSubFormulas().get(1);
							if (ec.isPresent(role, filler)) {
								Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
								if (definer_map.get(filler) != null) {
									AtomicConcept definer = definer_map.get(filler);
									disjunct.getSubFormulas().set(1, definer);
									output_list.add(formula);
									break;
								
								} else if (definer_map.get(filler_negated) != null) {
									Formula definer_negated = new Negation(definer_map.get(filler_negated));
									disjunct.getSubFormulas().set(1, definer_negated);
									output_list.add(formula);
									
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(definer_map.get(filler_negated));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
									}
									
									break;
									
								} else {	
									AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(filler, definer);
									disjunct.getSubFormulas().set(1, definer);
									output_list.add(formula);
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(new Negation(definer));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
									}
									break;
								}
								
							} else {
								output_list.add(formula);
							}
						}
					}
					// Case: >= 2
				} else {

					for (int i = 0; i < disjuncts.size(); i++) {
						Formula disjunct = disjuncts.get(i);
						if (ec.isPresent(role, disjunct)) {
							if ((fc.positive(role, formula) + fc.negative(role, formula))
									- (fc.positive(role, disjunct) + fc.negative(role, disjunct)) > 0) {
								
								Formula disjunct_negated = pp.getSimplifiedForm(new Negation(disjunct.clone()));
								
								if (definer_map.get(disjunct) != null) {
									AtomicConcept definer = definer_map.get(disjunct);
									disjuncts.set(i, definer);
									output_list.addAll(introduceDefiners(role, formula));
									break;
								
								} else if (definer_map.get(disjunct_negated) != null) {
									Formula definer_negated = new Negation(definer_map.get(disjunct_negated));
									disjuncts.set(i, definer_negated);						
									List<Formula> disjunct_list = new ArrayList<>();
									disjunct_list.add(definer_map.get(disjunct_negated));
									disjunct_list.add(disjunct);
									output_list.addAll(introduceDefiners(role, formula));
									output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
									break;
									
								} else {	
									AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(disjunct, definer);
									disjuncts.set(i, definer);
									List<Formula> disjunct_list = new ArrayList<>();
									disjunct_list.add(new Negation(definer));
									disjunct_list.add(disjunct);
									output_list.addAll(introduceDefiners(role, formula));
									output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
									break;
								}
								
							} else {
								
								Formula filler = disjunct.getSubFormulas().get(1);
								Formula filler_negated = pp.getSimplifiedForm(new Negation(filler.clone()));
								
								if (definer_map.get(filler) != null) {
									AtomicConcept definer = definer_map.get(filler);
									disjunct.getSubFormulas().set(1, definer);
									output_list.add(formula);
									break;
								
								} else if (definer_map.get(filler_negated) != null) {
									Formula definer_negated = new Negation(definer_map.get(filler_negated));
									disjunct.getSubFormulas().set(1, definer_negated);
									output_list.add(formula);
									
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(definer_map.get(filler_negated));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
									}
									
									break;
									
								} else {	
									AtomicConcept definer = new AtomicConcept("Definer_" + AtomicConcept.getDefiner_index());
									AtomicConcept.setDefiner_index(AtomicConcept.getDefiner_index() + 1);
									definer_set.add(definer);
									definer_map.put(filler, definer);
									disjunct.getSubFormulas().set(1, definer);
									output_list.add(formula);
									List<Formula> conjunct_list = pp.getCNF(pp.getSimplifiedForm(Collections.singletonList(filler.clone())));
									for (Formula conjunct : conjunct_list) {
										List<Formula> disjunct_list = new ArrayList<>();
										disjunct_list.add(new Negation(definer));
										if (conjunct instanceof Or) {
											disjunct_list.addAll(conjunct.getSubFormulas());
										} else {
											disjunct_list.add(conjunct);	
										}
										output_list.addAll(introduceDefiners(role, new Or(disjunct_list)));
									}
									break;
								}
							}
						}
					}
				}

			} else {
				output_list.add(formula);
			}

		} else {
			output_list.add(formula);
		}
		
		//System.out.println("output_list = " + output_list);

		return output_list;
	}

	public List<List<Formula>> getCombinations(List<Formula> input_list) {

		List<List<Formula>> output_list = new ArrayList<>();

		int nCnt = input_list.size();

		int nBit = (0xFFFFFFFF >>> (32 - nCnt));

		for (int i = 1; i <= nBit; i++) {
			output_list.add(new ArrayList<>());
			for (int j = 0; j < nCnt; j++) {
				if ((i << (31 - j)) >> 31 == -1) {
					output_list.get(i - 1).add(input_list.get(j));
				}
			}
		}

		return output_list;
	}

	public List<Formula> Ackermann_A(AtomicConcept concept, List<Formula> formula_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		List<Formula> positive_star_premises = new ArrayList<>();
		List<Formula> positive_exists_premises = new ArrayList<>();
		List<Formula> positive_forall_premises = new ArrayList<>();
		List<Formula> negative_star_premises = new ArrayList<>();
		List<Formula> negative_exists_premises = new ArrayList<>();
		List<Formula> negative_forall_premises = new ArrayList<>();

		EChecker ec = new EChecker();

		for (Formula formula : formula_list) {
			
			if (!ec.isPresent(concept, formula)) {
				output_list.add(formula);

			} else if (formula.equals(concept)) {
				positive_star_premises.add(formula);

			} else if (formula.equals(new Negation(concept))) {
				negative_star_premises.add(formula);

			} else if (formula instanceof Exists && formula.getSubFormulas().get(1).equals(concept)) {
				positive_exists_premises.add(formula);

			} else if (formula instanceof Exists && formula.getSubFormulas().get(1).equals(new Negation(concept))) {
				negative_exists_premises.add(formula);

			} else if (formula instanceof Forall && formula.getSubFormulas().get(1).equals(concept)) {
				positive_forall_premises.add(formula);

			} else if (formula instanceof Forall && formula.getSubFormulas().get(1).equals(new Negation(concept))) {
				negative_forall_premises.add(formula);

			} else if (formula instanceof Or) {
				
				List<Formula> disjunct_list = formula.getSubFormulas();

				if (disjunct_list.contains(concept)) {
					positive_star_premises.add(formula);

				} else if (disjunct_list.contains(new Negation(concept))) {
					negative_star_premises.add(formula);
			
				} else {
					for (Formula disjunct : disjunct_list) {
						if (disjunct instanceof Exists && disjunct.getSubFormulas().get(1).equals(concept)) {
							positive_exists_premises.add(formula);
							break;
						} else if (disjunct instanceof Exists
								&& disjunct.getSubFormulas().get(1).equals(new Negation(concept))) {
							negative_exists_premises.add(formula);
							break;
						} else if (disjunct instanceof Forall && disjunct.getSubFormulas().get(1).equals(concept)) {
							positive_forall_premises.add(formula);
							break;
						} else if (disjunct instanceof Forall
								&& disjunct.getSubFormulas().get(1).equals(new Negation(concept))) {
							negative_forall_premises.add(formula);
							break;
						}
					}
				}

			} else {
				output_list.add(formula);
			}
		}
		//System.out.println("=====================================================");
		/*System.out.println("positive_star_premises = " + positive_star_premises.size());
		System.out.println("positive_exists_premises = " + positive_exists_premises.size());
		System.out.println("positive_forall_premises = " + positive_forall_premises.size());
		System.out.println("negative_star_premises = " + negative_star_premises.size());
		System.out.println("negative_exists_premises = " + negative_exists_premises.size());
		System.out.println("negative_forall_premises = " + negative_forall_premises.size());*/
		//

		if (!negative_star_premises.isEmpty()) {
			
			if (negative_star_premises.contains(new Negation(concept))) {
				
				Formula ns_def = BottomConcept.getInstance();	
				
				if (!positive_star_premises.isEmpty()) {
					for (Formula ps_premise : positive_star_premises) {
						output_list.add(AckermannReplace(concept, ps_premise, ns_def));
					}
				}
				if (!positive_exists_premises.isEmpty()) {
					for (Formula pe_premise : positive_exists_premises) {
						output_list.add(AckermannReplace(concept, pe_premise, ns_def));
					}
				}
				if (!positive_forall_premises.isEmpty()) {
					for (Formula pf_premise : positive_forall_premises) {
						output_list.add(AckermannReplace(concept, pf_premise, ns_def));
					}
				}
				
			} else {
				
				List<Formula> and_list = new ArrayList<>();
				
				for (Formula ns_premise : negative_star_premises) {
					
					Formula ns_def = null;
					
					List<Formula> def_disjunct_list = new ArrayList<>(ns_premise.getSubFormulas());
					def_disjunct_list.remove(new Negation(concept));
					
					if (def_disjunct_list.size() == 1) {
						ns_def = def_disjunct_list.get(0);
					} else {
						ns_def = new Or(def_disjunct_list);
					}
					and_list.add(ns_def);
					if (!positive_star_premises.isEmpty()) {
						for (Formula ps_premise : positive_star_premises) {
							output_list.add(AckermannReplace(concept, ps_premise, ns_def));
						}
					}
					if (!positive_forall_premises.isEmpty()) {
						for (Formula pf_premise : positive_forall_premises) {
							output_list.add(AckermannReplace(concept, pf_premise, ns_def));
						}
					}
				}
				
				if (!positive_exists_premises.isEmpty()) {
					
					Formula ns_def_and = null;
					
					if (and_list.size() == 1) {
					    ns_def_and = and_list.get(0);
					} else {
						ns_def_and = new And(and_list);
					}				
					for (Formula pe_premise : positive_exists_premises) {
						output_list.add(AckermannReplace(concept, pe_premise, ns_def_and));
					}
				}								
			}
		}

		//
		if (!positive_star_premises.isEmpty()) {
						
			if (positive_star_premises.contains(concept)) {
				
				Formula ps_def = TopConcept.getInstance();	
				
				if (!negative_exists_premises.isEmpty()) {
					for (Formula ne_premise : negative_exists_premises) {
						output_list.add(AckermannReplace(concept, ne_premise, ps_def));
					}
				}
				if (!negative_forall_premises.isEmpty()) {
					for (Formula nf_premise : negative_forall_premises) {
						output_list.add(AckermannReplace(concept, nf_premise, ps_def));
					}
				}
				
			} else {
				
				List<Formula> or_list = new ArrayList<>();
				
				for (Formula ps_premise : positive_star_premises) {
					
					Formula ps_def = null;
					
					List<Formula> def_disjunct_list = new ArrayList<>(ps_premise.getSubFormulas());
					def_disjunct_list.remove(concept);
					
					if (def_disjunct_list.size() == 1) {
						ps_def = new Negation(def_disjunct_list.get(0));
					} else {
						ps_def = new Negation(new Or(def_disjunct_list));
					}
					or_list.add(ps_def);
					if (!negative_forall_premises.isEmpty()) {
						for (Formula nf_premise : negative_forall_premises) {
							output_list.add(AckermannReplace(concept, nf_premise, ps_def));
						}
					}
				}
				
				if (!negative_exists_premises.isEmpty()) {
					Formula ps_def_or = null;
					if (or_list.size() == 1) {
					    ps_def_or = or_list.get(0);
					} else {
						ps_def_or = new Or(or_list);
					}				
					for (Formula ne_premise : negative_exists_premises) {
						output_list.add(AckermannReplace(concept, ne_premise, ps_def_or));
					}
				}								
			}
		}

		//&& (!positive_exists_premises.isEmpty() || !positive_forall_premises.isEmpty())
		if (!negative_exists_premises.isEmpty()) {

			if (!positive_exists_premises.isEmpty()) {
				if (negative_star_premises.isEmpty()) {
					for (Formula pe_premise : positive_exists_premises) {
						output_list.add(PurifyPositive(concept, pe_premise));
					}
				}
				if (positive_star_premises.isEmpty()) {
					for (Formula ne_premise : negative_exists_premises) {
						output_list.add(PurifyNegative(concept, ne_premise));
					}
				}
			}

			if (!positive_forall_premises.isEmpty()) {
				
				if (positive_star_premises.isEmpty() && positive_exists_premises.isEmpty()) {
					for (Formula ne_premise : negative_exists_premises) {
						output_list.add(PurifyNegative(concept, ne_premise));
					}
				}

				for (Formula pf_premise : positive_forall_premises) {
					List<Formula> pf_disjunct_list = null;
					Formula pf_role = null;
					if (pf_premise instanceof Forall) {
						pf_disjunct_list = new ArrayList<>();
						pf_role = pf_premise.getSubFormulas().get(0);
					} else {
						pf_disjunct_list = new ArrayList<>(pf_premise.getSubFormulas());
						for (Formula pf_disjunct : pf_disjunct_list) {
							if (ec.isPresent(concept, pf_disjunct)) {
								pf_role = pf_disjunct.getSubFormulas().get(0);
								pf_disjunct_list.remove(pf_disjunct);
								break;
							}
						}
					}

					for (Formula ne_premise : negative_exists_premises) {
						if (ne_premise instanceof Exists) {
							Formula ne_role = ne_premise.getSubFormulas().get(0);
							if (ne_role.equals(pf_role)) {
								List<Formula> E_list = new ArrayList<>();
								E_list.addAll(pf_disjunct_list);
								if (E_list.isEmpty()) {

								} else if (E_list.size() == 1) {
									output_list.add(E_list.get(0));
								} else {
									output_list.add(new Or(E_list));
								}
							}

						} else {
							List<Formula> ne_disjunct_list = new ArrayList<>(ne_premise.getSubFormulas());
							for (Formula ne_disjunct : ne_disjunct_list) {
								if (ec.isPresent(concept, ne_disjunct)) {
									Formula ne_role = ne_disjunct.getSubFormulas().get(0);
									if (ne_role.equals(pf_role)) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pf_disjunct_list);
										ne_disjunct_list.remove(ne_disjunct);
										E_list.addAll(ne_disjunct_list);
										if (E_list.size() == 1) {
											output_list.add(E_list.get(0));
											break;
										} else {
											output_list.add(new Or(E_list));
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}

		//
		if (!negative_forall_premises.isEmpty()) {

			if (!positive_exists_premises.isEmpty()) {

				if (negative_star_premises.isEmpty() && negative_exists_premises.isEmpty()) {
					for (Formula pe_premise : positive_exists_premises) {
						output_list.add(PurifyPositive(concept, pe_premise));
					}
				}

				for (Formula pe_premise : positive_exists_premises) {

					List<Formula> pe_disjunct_list = null;
					Formula pe_role = null;
					if (pe_premise instanceof Exists) {
						pe_disjunct_list = new ArrayList<>();
						pe_role = pe_premise.getSubFormulas().get(0);

					} else {
						pe_disjunct_list = new ArrayList<>(pe_premise.getSubFormulas());
						for (Formula pe_disjunct : pe_disjunct_list) {
							if (ec.isPresent(concept, pe_disjunct)) {
								pe_role = pe_disjunct.getSubFormulas().get(0);
								pe_disjunct_list.remove(pe_disjunct);
								break;
							}
						}
					}

					for (Formula nf_premise : negative_forall_premises) {

						if (nf_premise instanceof Forall) {
							Formula nf_role = nf_premise.getSubFormulas().get(0);
							if (nf_role.equals(pe_role)) {
								List<Formula> E_list = new ArrayList<>();
								E_list.addAll(pe_disjunct_list);
								if (E_list.isEmpty()) {

								} else if (E_list.size() == 1) {
									output_list.add(E_list.get(0));
								} else {
									output_list.add(new Or(E_list));
								}
							}

						} else {
							List<Formula> nf_disjunct_list = new ArrayList<>(nf_premise.getSubFormulas());
							for (Formula nf_disjunct : nf_disjunct_list) {
								if (ec.isPresent(concept, nf_disjunct)) {
									Formula nf_role = nf_disjunct.getSubFormulas().get(0);
									if (nf_role.equals(pe_role)) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pe_disjunct_list);
										nf_disjunct_list.remove(nf_disjunct);
										E_list.addAll(nf_disjunct_list);
										if (E_list.size() == 1) {
											output_list.add(E_list.get(0));
											break;
										} else {
											output_list.add(new Or(E_list));
											break;
										}
									}
								}
							}
						}
					}
				}
			}

			if (!positive_forall_premises.isEmpty()) {

				for (Formula pf_premise : positive_forall_premises) {
					
					List<Formula> pf_disjunct_list = null;
					Formula pf_role = null;
					if (pf_premise instanceof Forall) {
						pf_disjunct_list = new ArrayList<>();
						pf_role = pf_premise.getSubFormulas().get(0);
					} else {
						pf_disjunct_list = new ArrayList<>(pf_premise.getSubFormulas());
						for (Formula pf_disjunct : pf_disjunct_list) {
							if (ec.isPresent(concept, pf_disjunct)) {
								pf_role = pf_disjunct.getSubFormulas().get(0);
								pf_disjunct_list.remove(pf_disjunct);
								break;
							}
						}
					}

					for (Formula nf_premise : negative_forall_premises) {
						
						if (nf_premise instanceof Forall) {
							Formula nf_role = nf_premise.getSubFormulas().get(0);
							if (nf_role.equals(pf_role)) {
								List<Formula> E_list = new ArrayList<>();
								E_list.addAll(pf_disjunct_list);
								if (E_list.isEmpty()) {
									output_list.add(new Forall(nf_role, BottomConcept.getInstance()));
								} else {
									E_list.add(new Forall(nf_role, BottomConcept.getInstance()));
									output_list.add(new Or(E_list));
								}
							}

						} else {

							List<Formula> nf_disjunct_list = new ArrayList<>(nf_premise.getSubFormulas());
							for (Formula nf_disjunct : nf_disjunct_list) {
								if (ec.isPresent(concept, nf_disjunct)) {
									Formula nf_role = nf_disjunct.getSubFormulas().get(0);
									if (nf_role.equals(pf_role)) {
										List<Formula> E_list = new ArrayList<>();
										E_list.addAll(pf_disjunct_list);
										nf_disjunct_list.remove(nf_disjunct);
										E_list.addAll(nf_disjunct_list);
										E_list.add(new Forall(nf_role, BottomConcept.getInstance()));
										output_list.add(new Or(E_list));
										break;
									}
								}
							}
						}
					}
				}
			}
		}

		//System.out.println("The output list of Ackermann_A: " + output_list);
		return output_list;
	}
	
	
	public List<Formula> Ackermann_R(AtomicRole role, List<Formula> formula_list)
			throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();

		List<Formula> positive_TBox_premises = new ArrayList<>();
		List<Formula> negative_TBox_premises = new ArrayList<>();

		EChecker ec = new EChecker();

		for (Formula formula : formula_list) {
			if (!ec.isPresent(role, formula)) {
				output_list.add(formula);

			} else if (formula instanceof Exists) {
				positive_TBox_premises.add(formula);

			} else if (formula instanceof Forall) {
				negative_TBox_premises.add(formula);

			} else if (formula instanceof Or) {
				List<Formula> disjunct_list = formula.getSubFormulas();

				for (Formula disjunct : disjunct_list) {
					if (disjunct instanceof Exists && disjunct.getSubFormulas().get(0).equals(role)) {
						positive_TBox_premises.add(formula);
						break;
					} else if (disjunct instanceof Forall && disjunct.getSubFormulas().get(0).equals(role)) {
						negative_TBox_premises.add(formula);
						break;
					}
				}

			}
		}
		
		System.out.println("positive_TBox_premises = " + positive_TBox_premises);
		System.out.println("negative_TBox_premises = " + negative_TBox_premises);

		if (positive_TBox_premises.isEmpty() || negative_TBox_premises.isEmpty()) {
			return output_list;
		
		} else {
			
			PreProcessor pp = new PreProcessor();
			
			List<List<Formula>> combination_list = getCombinations(negative_TBox_premises);
			//System.out.println("combination_list = " + combination_list);
			
			for (Formula pt_premise : positive_TBox_premises) {
				List<Formula> pt_C_list = new ArrayList<>();
				List<Formula> pt_D_list = new ArrayList<>();

				if (pt_premise instanceof Exists) {
					if (pt_premise.getSubFormulas().get(1) instanceof And) {
						pt_D_list.addAll(pt_premise.getSubFormulas().get(1).getSubFormulas());
					} else {
						pt_D_list.add(pt_premise.getSubFormulas().get(1));
					}

				} else {
					List<Formula> pt_disjunct_list = pt_premise.getSubFormulas();

					for (Formula pt_disjunct : pt_disjunct_list) {
						if (pt_disjunct instanceof Exists && pt_disjunct.getSubFormulas().get(0).equals(role)) {
							if (pt_disjunct.getSubFormulas().get(1) instanceof And) {
								pt_D_list.addAll(pt_disjunct.getSubFormulas().get(1).getSubFormulas());
							} else {
								pt_D_list.add(pt_disjunct.getSubFormulas().get(1));
							}
						} else {
							pt_C_list.add(pt_disjunct);
						}
					}

				}

				for (List<Formula> combination : combination_list) {
					List<Formula> CE_list = new ArrayList<>(pt_C_list);
					List<Formula> DF_list = new ArrayList<>(pt_D_list);

					for (Formula nt_premise : combination) {
						if (nt_premise instanceof Forall) {
							if (nt_premise.getSubFormulas().get(1) instanceof And) {
								DF_list.addAll(nt_premise.getSubFormulas().get(1).getSubFormulas());
							} else {
								DF_list.add(nt_premise.getSubFormulas().get(1));
							}

						} else {
							List<Formula> nt_disjunct_list = nt_premise.getSubFormulas();
							for (Formula nt_disjunct : nt_disjunct_list) {
								if (nt_disjunct instanceof Forall
										&& nt_disjunct.getSubFormulas().get(0).equals(role)) {
									if (nt_disjunct.getSubFormulas().get(1) instanceof And) {
										DF_list.addAll(nt_disjunct.getSubFormulas().get(1).getSubFormulas());
									} else {
										DF_list.add(nt_disjunct.getSubFormulas().get(1));
									}

								} else {
									CE_list.add(nt_disjunct);
								}
							}
						}
					}

					Formula DF = null;

					if (DF_list.size() == 1) {
						DF = DF_list.get(0);
					} else {
						DF = new And(DF_list);
					}
					
					//System.out.println("DF = " + DF);
					//System.out.println("pp.getSimplifiedForm(DF) = " + pp.getSimplifiedForm(DF));

					if (pp.getSimplifiedForm(DF.clone()) == BottomConcept.getInstance()) {

						if (CE_list.isEmpty()) {
							continue;
						} else if (CE_list.size() == 1) {
							output_list.add(CE_list.get(0));
							continue;
						} else {
							output_list.add(new Or(CE_list));
							continue;
						}
					}
				}
			}			
		}
		
		return output_list;
	}

			
	/*public List<Formula> Ackermann_R(AtomicRole role, List<Formula> formula_list)
			throws CloneNotSupportedException {

		// System.out.println("role = " + role);
		// System.out.println("formula_list = " + formula_list);

		List<Formula> output_list = new ArrayList<>();

		List<Formula> positive_RBox_premises = new ArrayList<>();
		List<Formula> negative_RBox_premises = new ArrayList<>();
		List<Formula> positive_TBox_premises = new ArrayList<>();
		List<Formula> negative_TBox_premises = new ArrayList<>();

		EChecker ec = new EChecker();
		FChecker fc = new FChecker();

		for (Formula formula : formula_list) {
			if (fc.positive(role, formula) + fc.negative(role, formula) > 1) {
				return formula_list;
						
			} else if (!ec.isPresent(role, formula)) {
				output_list.add(formula);

			} else if (formula.equals(role)) {
				positive_RBox_premises.add(formula);

			} else if (formula.equals(new Negation(role))) {
				negative_RBox_premises.add(formula);

			} else if (formula instanceof Exists && formula.getSubFormulas().get(0).equals(role)) {
				positive_TBox_premises.add(formula);

			} else if (formula instanceof Forall && formula.getSubFormulas().get(0).equals(role)) {
				negative_TBox_premises.add(formula);

			} else if (formula instanceof Or) {
				List<Formula> disjunct_list = formula.getSubFormulas();

				if (disjunct_list.contains(role)) {
					positive_RBox_premises.add(formula);

				} else if (disjunct_list.contains(new Negation(role))) {
					negative_RBox_premises.add(formula);

				} else {
					for (Formula disjunct : disjunct_list) {
						if (disjunct instanceof Exists && disjunct.getSubFormulas().get(0).equals(role)) {
							positive_TBox_premises.add(formula);
							break;
						} else if (disjunct instanceof Forall && disjunct.getSubFormulas().get(0).equals(role)) {
							negative_TBox_premises.add(formula);
							break;
						}
					}
				}
			}
		}

		if (negative_TBox_premises.isEmpty() && negative_RBox_premises.isEmpty()) {
			return output_list;
		}

		if (positive_TBox_premises.isEmpty() && positive_RBox_premises.isEmpty()) {
			return output_list;
		}

		//
		if (!negative_RBox_premises.isEmpty()) {

			if (negative_RBox_premises.contains(new Negation(role))) {
				if (!positive_RBox_premises.isEmpty()) {
					for (Formula pr_premise : positive_RBox_premises) {
						output_list.add(AckermannReplace(role, pr_premise, BottomRole.getInstance()));
					}
				}
				if (!positive_TBox_premises.isEmpty()) {
					for (Formula pt_premise : positive_TBox_premises) {
						output_list.add(AckermannReplace(role, pt_premise, BottomRole.getInstance()));
					}
				}

			} else {

				for (Formula nr_premise : negative_RBox_premises) {

					Formula nr_def = null;
					List<Formula> nr_def_list = new ArrayList<>(nr_premise.getSubFormulas());
					nr_def_list.remove(new Negation(role));
					if (nr_def_list.size() == 1) {
						nr_def = nr_def_list.get(0);
					} else {
						nr_def = new Or(nr_def_list);
					}

					if (!positive_RBox_premises.isEmpty()) {
						for (Formula pr_premise : positive_RBox_premises) {
							output_list.add(AckermannReplace(role, pr_premise, nr_def));
						}
					}
					if (!positive_TBox_premises.isEmpty()) {
						for (Formula pt_premise : positive_TBox_premises) {
							output_list.add(AckermannReplace(role, pt_premise, nr_def));
						}
					}
				}
			}
		}

		//
		if (!positive_RBox_premises.isEmpty()) {

			if (positive_RBox_premises.contains(role)) {
				if (!negative_TBox_premises.isEmpty()) {
					for (Formula nt_premise : negative_TBox_premises) {
						output_list.add(AckermannReplace(role, nt_premise, TopRole.getInstance()));
					}
				}

			} else {

				for (Formula pr_premise : positive_RBox_premises) {

					Formula pr_def = null;
					List<Formula> pr_def_list = new ArrayList<>(pr_premise.getSubFormulas());
					pr_def_list.remove(role);
					if (pr_def_list.size() == 1) {
						pr_def = new Negation(pr_def_list.get(0));
					} else {
						pr_def = new Negation(new Or(pr_def_list));
					}
					if (!negative_TBox_premises.isEmpty()) {
						for (Formula nt_premise : negative_TBox_premises) {
							output_list.add(AckermannReplace(role, nt_premise, pr_def));
						}
					}
				}
			}
		}

		//
		if (!positive_TBox_premises.isEmpty() && !negative_TBox_premises.isEmpty()) {
			
			PreProcessor pp = new PreProcessor();

			if (negative_RBox_premises.isEmpty()) {
				
				for (Formula pt_premise : positive_TBox_premises) {
					List<Formula> pt_C_list = new ArrayList<>();
					List<Formula> pt_D_list = new ArrayList<>();

					if (pt_premise instanceof Exists) {
						if (pt_premise.getSubFormulas().get(1) instanceof And) {
							pt_D_list.addAll(pt_premise.getSubFormulas().get(1).getSubFormulas());
						} else {
							pt_D_list.add(pt_premise.getSubFormulas().get(1));
						}

					} else {
						List<Formula> pt_disjunct_list = pt_premise.getSubFormulas();

						for (Formula pt_disjunct : pt_disjunct_list) {
							if (pt_disjunct instanceof Exists && pt_disjunct.getSubFormulas().get(0).equals(role)) {
								if (pt_disjunct.getSubFormulas().get(1) instanceof And) {
									pt_D_list.addAll(pt_disjunct.getSubFormulas().get(1).getSubFormulas());
								} else {
									pt_D_list.add(pt_disjunct.getSubFormulas().get(1));
								}
							} else {
								pt_C_list.add(pt_disjunct);
							}
						}
					}
						
					List<List<Formula>> combination_list = getCombinations(negative_TBox_premises);
						
					for (List<Formula> combination : combination_list) {
						List<Formula> CE_list = new ArrayList<>(pt_C_list);
						List<Formula> DF_list = new ArrayList<>(pt_D_list);

						for (Formula nt_premise : combination) {
							if (nt_premise instanceof Forall) {
								if (nt_premise.getSubFormulas().get(1) instanceof And) {
									DF_list.addAll(nt_premise.getSubFormulas().get(1).getSubFormulas());
								} else {
									DF_list.add(nt_premise.getSubFormulas().get(1));
								}

							} else {
								List<Formula> nt_disjunct_list = nt_premise.getSubFormulas();
								for (Formula nt_disjunct : nt_disjunct_list) {
									if (nt_disjunct instanceof Forall
											&& nt_disjunct.getSubFormulas().get(0).equals(role)) {
										if (nt_disjunct.getSubFormulas().get(1) instanceof And) {
											DF_list.addAll(nt_disjunct.getSubFormulas().get(1).getSubFormulas());
										} else {
											DF_list.add(nt_disjunct.getSubFormulas().get(1));
										}

									} else {
										CE_list.add(nt_disjunct);
									}
								}
							}
						}

						Formula DF = null;

						if (DF_list.size() == 1) {
							DF = DF_list.get(0);
						} else {
							DF = new And(DF_list);
						}

						if (pp.getSimplifiedForm(DF) == BottomConcept.getInstance() || pp.getSimplifiedForm(DF) == BottomConcept.getInstance()) {

							if (CE_list.isEmpty()) {
								continue;
							} else if (CE_list.size() == 1) {
								output_list.add(CE_list.get(0));
								continue;
							} else {
								output_list.add(new Or(CE_list));
								continue;
							}

						} else {
							continue;
						}
					}
				}

			} else {

				List<List<Formula>> combination_list = getCombinations(negative_TBox_premises);

				for (Formula nr_premise : negative_RBox_premises) {
					Formula nr_def = null;
					if (nr_premise.equals(new Negation(role))) {
						nr_def = BottomRole.getInstance();
					} else {
						List<Formula> nr_def_list = new ArrayList<>(nr_premise.getSubFormulas());
						nr_def_list.remove(new Negation(role));
						if (nr_def_list.size() == 1) {
							nr_def = nr_def_list.get(0);
						} else {
							nr_def = new Or(nr_def_list);
						}
					}

					for (Formula pt_premise : positive_TBox_premises) {
						List<Formula> pt_C_list = new ArrayList<>();
						List<Formula> pt_D_list = new ArrayList<>();

						if (pt_premise instanceof Exists) {
							if (pt_premise.getSubFormulas().get(1) instanceof And) {
								pt_D_list.addAll(pt_premise.getSubFormulas().get(1).getSubFormulas());
							} else {
								pt_D_list.add(pt_premise.getSubFormulas().get(1));
							}

						} else {
							List<Formula> pt_disjunct_list = pt_premise.getSubFormulas();

							for (Formula pt_disjunct : pt_disjunct_list) {
								if (pt_disjunct instanceof Exists && pt_disjunct.getSubFormulas().get(0).equals(role)) {
									if (pt_disjunct.getSubFormulas().get(1) instanceof And) {
										pt_D_list.addAll(pt_disjunct.getSubFormulas().get(1).getSubFormulas());
									} else {
										pt_D_list.add(pt_disjunct.getSubFormulas().get(1));
									}
								} else {
									pt_C_list.add(pt_disjunct);
								}
							}

						}

						for (List<Formula> combination : combination_list) {
							List<Formula> CE_list = new ArrayList<>(pt_C_list);
							List<Formula> DF_list = new ArrayList<>(pt_D_list);

							for (Formula nt_premise : combination) {
								if (nt_premise instanceof Forall) {
									if (nt_premise.getSubFormulas().get(1) instanceof And) {
										DF_list.addAll(nt_premise.getSubFormulas().get(1).getSubFormulas());
									} else {
										DF_list.add(nt_premise.getSubFormulas().get(1));
									}

								} else {
									List<Formula> nt_disjunct_list = nt_premise.getSubFormulas();
									for (Formula nt_disjunct : nt_disjunct_list) {
										if (nt_disjunct instanceof Forall
												&& nt_disjunct.getSubFormulas().get(0).equals(role)) {
											if (nt_disjunct.getSubFormulas().get(1) instanceof And) {
												DF_list.addAll(nt_disjunct.getSubFormulas().get(1).getSubFormulas());
											} else {
												DF_list.add(nt_disjunct.getSubFormulas().get(1));
											}

										} else {
											CE_list.add(nt_disjunct);
										}
									}
								}
							}

							Formula DF = null;

							if (DF_list.size() == 1) {
								DF = DF_list.get(0);
							} else {
								DF = new And(DF_list);
							}

							if (pp.getSimplifiedForm(DF) == BottomConcept.getInstance()
									|| pp.getSimplifiedForm(DF) == BottomConcept.getInstance()) {

								if (CE_list.isEmpty()) {
									continue;
								} else if (CE_list.size() == 1) {
									output_list.add(CE_list.get(0));
									continue;
								} else {
									output_list.add(new Or(CE_list));
									continue;
								}

							} else {

								if (CE_list.isEmpty()) {
									output_list.add(new Exists(nr_def, DF));
									continue;
								} else {
									CE_list.add(new Exists(nr_def, DF));
									output_list.add(new Or(CE_list));
									continue;
								}
							}
						}
					}
				}
			}
		} 
		
		return output_list;
	}*/
	
	
	
	public List<Formula> AckermannPositive(AtomicConcept concept, List<Formula> input_list) throws CloneNotSupportedException {

		List<Formula> output_list = new ArrayList<>();
		List<Formula> toBeReplaced_list = new ArrayList<>();
		List<Formula> toReplace_list = new ArrayList<>();

		FChecker cf = new FChecker();

		for (Formula formula : input_list) {
			if (cf.positive(concept, formula) == 0) {
				toBeReplaced_list.add(formula);

			} else {
				toReplace_list.add(formula);
			}
		}

		Formula definition = null;
		List<Formula> disjunct_list = new ArrayList<>();

		for (Formula toReplace : toReplace_list) {
			if (toReplace.equals(concept)) {
				definition = TopConcept.getInstance();
				break;
				
			} else {
				List<Formula> other_list = new ArrayList<>(toReplace.getSubFormulas());
				other_list.remove(concept);
				if (other_list.size() == 1) {
					disjunct_list.add(new Negation(other_list.get(0)));
					continue;
				} else {
					disjunct_list.add(new Negation(new Or(other_list)));
					continue;
				}
			}
		}

		if (definition != TopConcept.getInstance()) {
			if (disjunct_list.size() == 1) {
				definition = disjunct_list.get(0);
			} else {
				definition = new Or(disjunct_list);
			}
		}

		for (Formula toBeReplaced : toBeReplaced_list) {
			output_list.add(AckermannReplace(concept, toBeReplaced, definition));
		}

		return output_list;
	}
	
	public List<Formula> AckermannNegative(AtomicConcept concept, List<Formula> input_list)
			throws CloneNotSupportedException {
		
		List<Formula> output_list = new ArrayList<>();
		List<Formula> toBeReplaced_list = new ArrayList<>();
		List<Formula> toReplace_list = new ArrayList<>();

		FChecker cf = new FChecker();

		for (Formula formula : input_list) {
			if (cf.negative(concept, formula) == 0) {
				toBeReplaced_list.add(formula);

			} else {
				toReplace_list.add(formula);
			}
		}

		Formula definition = null;
		List<Formula> disjunct_list = new ArrayList<>();

		for (Formula toReplace : toReplace_list) {
			if (toReplace.equals(new Negation(concept))) {
				definition = BottomConcept.getInstance();
				break;
				
			} else {
				List<Formula> other_list = new ArrayList<>(toReplace.getSubFormulas());
				other_list.remove(new Negation(concept));
				if (other_list.size() == 1) {
					disjunct_list.add(other_list.get(0));
					continue;
				} else {
					disjunct_list.add(new Or(other_list));
					continue;
				}
			}
		}

		if (definition != BottomConcept.getInstance()) {
			if (disjunct_list.size() == 1) {
				definition = disjunct_list.get(0);
			} else {
				definition = new And(disjunct_list);
			}
		}

		for (Formula toBeReplaced : toBeReplaced_list) {
			output_list.add(AckermannReplace(concept, toBeReplaced, definition));
		}

		return output_list;
	}

	public List<Formula> PurifyPositive(AtomicRole role, List<Formula> input_list)
			throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			if (cf.positive(role, formula) == 0) {
				output_list.add(formula);
			} else {
				output_list.add(PurifyPositive(role, formula));
			}
		}

		return output_list;
	}

	public List<Formula> PurifyPositive(AtomicConcept concept, List<Formula> input_list)
			throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			if (cf.positive(concept, formula) == 0) {
				output_list.add(formula);
			} else {
				output_list.add(PurifyPositive(concept, formula));
			}
		}

		return output_list;
	}

	public List<Formula> PurifyNegative(AtomicRole role, List<Formula> input_list)
			throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> output_list = new ArrayList<>();

		for (Formula formula : input_list) {
			if (cf.negative(role, formula) == 0) {
				output_list.add(formula);
			} else {
				output_list.add(PurifyNegative(role, formula));
			}
		}

		return output_list;
	}

	public List<Formula> PurifyNegative(AtomicConcept concept, List<Formula> inputList)
			throws CloneNotSupportedException {

		FChecker cf = new FChecker();

		List<Formula> outputList = new ArrayList<>();

		for (Formula formula : inputList) {
			if (cf.negative(concept, formula) == 0) {
				outputList.add(formula);
			} else {
				outputList.add(PurifyNegative(concept, formula));
			}
		}

		return outputList;
	}

	public Formula AckermannReplace(AtomicRole role, Formula toBeReplaced, Formula definition) {

		if (toBeReplaced instanceof AtomicConcept) {
			return new AtomicConcept(toBeReplaced.getText());

		} else if (toBeReplaced instanceof AtomicRole) {
			return toBeReplaced.equals(role) ? definition : new AtomicRole(toBeReplaced.getText());

		} else if (toBeReplaced instanceof Individual) {
			return new Individual(toBeReplaced.getText());
		
		} else if (toBeReplaced instanceof Negation) {
			return new Negation(AckermannReplace(role, toBeReplaced.getSubFormulas().get(0), definition));

		} else if (toBeReplaced instanceof Exists) {
			return new Exists(AckermannReplace(role, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(role, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof Forall) {
			return new Forall(AckermannReplace(role, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(role, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof And) {
			List<Formula> conjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(AckermannReplace(role, conjunct, definition));
			}
			return new And(new_conjunct_list);

		} else if (toBeReplaced instanceof Or) {
			List<Formula> disjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(AckermannReplace(role, disjunct, definition));
			}
			return new Or(new_disjunct_list);

		}

		return toBeReplaced;
	}
	
	public Formula AckermannReplace(AtomicConcept concept, Formula toBeReplaced, Formula definition) {

		if (toBeReplaced instanceof AtomicConcept) {
			return toBeReplaced.equals(concept) ? definition : new AtomicConcept(toBeReplaced.getText());
			
		} else if (toBeReplaced instanceof AtomicRole) {
			return new AtomicRole(toBeReplaced.getText());

		} else if (toBeReplaced instanceof Individual) {
			return new Individual(toBeReplaced.getText());
		
		} else if (toBeReplaced instanceof Negation) {
			return new Negation(AckermannReplace(concept, toBeReplaced.getSubFormulas().get(0), definition));
			
		} else if (toBeReplaced instanceof Exists) {
			return new Exists(AckermannReplace(concept, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(concept, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof Forall) {
			return new Forall(AckermannReplace(concept, toBeReplaced.getSubFormulas().get(0), definition),
					AckermannReplace(concept, toBeReplaced.getSubFormulas().get(1), definition));

		} else if (toBeReplaced instanceof And) {
			List<Formula> conjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(AckermannReplace(concept, conjunct, definition));
			}
			return new And(new_conjunct_list);
			
		} else if (toBeReplaced instanceof Or) {
			List<Formula> disjunct_list = toBeReplaced.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(AckermannReplace(concept, disjunct, definition));
			}
			return new Or(new_disjunct_list);
			
		}
		
		return toBeReplaced;
	}
	
	public Formula PurifyPositive(AtomicRole role, Formula formula) {
		
		if (formula instanceof AtomicConcept) {
			return new AtomicConcept(formula.getText());
		
		} else if (formula instanceof AtomicRole) {
			return formula.equals(role) ? TopRole.getInstance() : new AtomicRole(formula.getText());
		
		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());
		
		} else if (formula instanceof Negation) {
			return new Negation(PurifyPositive(role, formula.getSubFormulas().get(0)));
			
		} else if (formula instanceof Exists) {
			return new Exists(PurifyPositive(role, formula.getSubFormulas().get(0)),
					PurifyPositive(role, formula.getSubFormulas().get(1)));
		
		} else if (formula instanceof Forall) {
			return new Forall(PurifyPositive(role, formula.getSubFormulas().get(0)),
					PurifyPositive(role, formula.getSubFormulas().get(1)));
			
		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyPositive(role, conjunct));
			}
			return new And(new_conjunct_list);
			
		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyPositive(role, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}
	
	public Formula PurifyNegative(AtomicRole role, Formula formula) {
		
		if (formula instanceof AtomicConcept) {
			return new AtomicConcept(formula.getText());
		
		} else if (formula instanceof AtomicRole) {
			return formula.equals(role) ? BottomRole.getInstance() : new AtomicRole(formula.getText());
		
		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());
		
		} else if (formula instanceof Negation) {
			return new Negation(PurifyNegative(role, formula.getSubFormulas().get(0)));
			
		} else if (formula instanceof Exists) {
			return new Exists(PurifyNegative(role, formula.getSubFormulas().get(0)),
					PurifyNegative(role, formula.getSubFormulas().get(1)));
		
		} else if (formula instanceof Forall) {
			return new Forall(PurifyNegative(role, formula.getSubFormulas().get(0)),
					PurifyNegative(role, formula.getSubFormulas().get(1)));
			
		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyNegative(role, conjunct));
			}
			return new And(new_conjunct_list);
			
		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyNegative(role, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}
	
	public Formula PurifyPositive(AtomicConcept concept, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return formula.equals(concept) ? TopConcept.getInstance() : new AtomicConcept(formula.getText());
			
		} else if (formula instanceof AtomicRole) {
			return new AtomicRole(formula.getText());

		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());
		
		} else if (formula instanceof Negation) {
			return new Negation(PurifyPositive(concept, formula.getSubFormulas().get(0)));
			
		} else if (formula instanceof Exists) {
			return new Exists(PurifyPositive(concept, formula.getSubFormulas().get(0)),
					PurifyPositive(concept, formula.getSubFormulas().get(1)));
		
		} else if (formula instanceof Forall) {
			return new Forall(PurifyPositive(concept, formula.getSubFormulas().get(0)),
					PurifyPositive(concept, formula.getSubFormulas().get(1)));
			
		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyPositive(concept, conjunct));
			}
			return new And(new_conjunct_list);
			
		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyPositive(concept, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}
			
	public Formula PurifyNegative(AtomicConcept concept, Formula formula) {

		if (formula instanceof AtomicConcept) {
			return formula.equals(concept) ? BottomConcept.getInstance() : new AtomicConcept(formula.getText());

		} else if (formula instanceof AtomicRole) {
			return new AtomicRole(formula.getText());

		} else if (formula instanceof Individual) {
			return new Individual(formula.getText());
		
		} else if (formula instanceof Negation) {
			return new Negation(PurifyNegative(concept, formula.getSubFormulas().get(0)));
			
		} else if (formula instanceof Exists) {
			return new Exists(PurifyNegative(concept, formula.getSubFormulas().get(0)),
					PurifyNegative(concept, formula.getSubFormulas().get(1)));

		} else if (formula instanceof Forall) {
			return new Forall(PurifyNegative(concept, formula.getSubFormulas().get(0)),
					PurifyNegative(concept, formula.getSubFormulas().get(1)));

		} else if (formula instanceof And) {
			List<Formula> conjunct_list = formula.getSubFormulas();
			List<Formula> new_conjunct_list = new ArrayList<>();
			for (Formula conjunct : conjunct_list) {
				new_conjunct_list.add(PurifyNegative(concept, conjunct));
			}
			return new And(new_conjunct_list);

		} else if (formula instanceof Or) {
			List<Formula> disjunct_list = formula.getSubFormulas();
			List<Formula> new_disjunct_list = new ArrayList<>();
			for (Formula disjunct : disjunct_list) {
				new_disjunct_list.add(PurifyNegative(concept, disjunct));
			}
			return new Or(new_disjunct_list);
		}

		return formula;
	}
	
}
