import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

public class AprioriMiner {

	static List<List<String>> transactionList = new ArrayList<List<String>>();
	static List<Rule> ruleList = new ArrayList<Rule>();
	static List<Set<String>> largeItemList = new ArrayList<Set<String>>();
	
	public static void main(String[] args) {

		double minSupport = 0, minConfidence = 0;
		String inputCsv = "";
		if(args.length > 0)
			inputCsv = args[0];
		if(args.length > 1)
			minSupport = Double.parseDouble(args[1]);
		if(args.length > 2)
			minConfidence = Double.parseDouble(args[2]);
		
		
		File inputFile = new File(inputCsv);
		List<Set<Set<String>>> largeItemSetList = new ArrayList<Set<Set<String>>>();
		
		//Getting individual Items and their counts
		Set<String> itemSet = new HashSet<String>();
		Map<String,Integer> itemSupportMap = new HashMap<String,Integer>();
		Map<Set<String>,Integer> largeItemSupportMap = new HashMap<Set<String>,Integer>();
		ValueComparator bvc =  new ValueComparator(largeItemSupportMap);
		TreeMap<Set<String>, Integer> sortedSupportMap = new TreeMap<Set<String>, Integer>(bvc);
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(inputFile));
			String line;
			while((line = br.readLine()) != null)
			{
				String[] items = line.split(",");
				List<String> transaction = new ArrayList<String>();
				for(String i : items)
				{
					if(itemSupportMap.containsKey(i))
					{
						int newCount = (itemSupportMap.get(i))+1;
						itemSupportMap.remove(i);
						itemSupportMap.put(i, newCount);
						itemSet.add(i);
					}
					else
					{
						itemSupportMap.put(i, 1);
					}
					transaction.add(i);
				}
				transactionList.add(transaction);
			}
			br.close();
			
			//Enter all level 0 large item sets in largeItemSetList
			Iterator<Entry<String, Integer>> iterator = itemSupportMap.entrySet().iterator();
			Set<String> temp = new HashSet<String>();
			while(iterator.hasNext())
			{
				Entry<String, Integer> entry = iterator.next();
				String key = (String)entry.getKey();
				Integer value = (Integer)entry.getValue();
				if((double)(value)/transactionList.size() >= minSupport)
				{
					temp.add(key);
				}
			}
			Set<Set<String>> setString = new HashSet<Set<String>>();
			setString.add(temp);
			largeItemSetList.add(setString);
			
			int k = 1;
			while(largeItemSetList.get(k-1).size() != 0)
			{
				Set<Set<String>> addToLargeItemSetList = new HashSet<Set<String>>(); //APRIORI-GEN---new candidates
				ArrayList<String> entryList = new ArrayList<String>(65536);
				ArrayList<Set<String>> preCkList = new ArrayList<Set<String>>(65536);
				ArrayList<Set<String>> ckList = new ArrayList<Set<String>>(65536);

				Iterator<Set<String>> iterator3 = largeItemSetList.get(k-1).iterator();
				while(iterator3.hasNext())
				{
					Set<String> s = iterator3.next();
					for(String string : s)
					{
						entryList.add(string);
					}
				}
				
				preCkList = getSubsets(entryList);
				
				//pruning candidates not included in previous level
				for(int i = 0; i < preCkList.size(); i ++)
				{
					if(preCkList.get(i).size() == k+1)
					{
						ckList.add(preCkList.get(i));
					}
				}
				
				for(int i = 0; i < ckList.size(); i ++)
				{
					//ELIMINATING ONES OCCURING IN PREVIOUS LARGE ITEM SET LIST (k-1)
					if(!largeItemSetList.get(k-1).containsAll(ckList.get(i)))
					{
						ckList.remove(i);
						continue;
					}
				}
				
				//candidates contained in transactions
				for(int i = 0; i < ckList.size(); i ++)
				{
					int count = 0;
					for(int j = 0; j < transactionList.size(); j ++)
					{
						if(transactionList.get(j).containsAll(ckList.get(i)))
							count ++;
					}
					if((double)((double)count/transactionList.size()) > minSupport)
					{
						addToLargeItemSetList.add(ckList.get(i));
					}
				}
				k ++;
				largeItemSetList.add(addToLargeItemSetList);
			}
			
			largeItemSetList.remove(largeItemSetList.size()-1);
			Set<Set<String>> tempSet = largeItemSetList.get(0);
			Iterator<Set<String>> tempIter = tempSet.iterator();
			Set<String> tempTempSet = tempIter.next();
			Iterator<String> tempTempIter = tempTempSet.iterator();
			Map<String, Integer> refMap = new HashMap<String, Integer>();
			while(tempTempIter.hasNext())
			{
				String enterStringInSet = tempTempIter.next();
				Set<String> newEntry = new HashSet<String>();
				newEntry.add(enterStringInSet);
				Integer newEntryCount = -1;
				
				Iterator<Entry<String, Integer>> innerIter = itemSupportMap.entrySet().iterator();
				while(innerIter.hasNext())
				{
					Entry<String, Integer> entry = innerIter.next();
					String key = (String)entry.getKey();
					Integer value = (Integer)entry.getValue();
					if(key.equals(enterStringInSet))
					{
						newEntryCount = value;
						refMap.put(key, value);
					}
				}
				largeItemSupportMap.put(newEntry, newEntryCount);
			}
			
			//populate largeItemSupportMap with k > 1
			for(int i = 1; i < largeItemSetList.size(); i ++)
			{
				Set<Set<String>> setSetString = largeItemSetList.get(i);
				Iterator<Set<String>> innerIter = setSetString.iterator();
				while(innerIter.hasNext())
				{
					Set<String> keySet = innerIter.next();
					Integer supportValue = 0;
					supportValue = getSupportCount(keySet);
					largeItemSupportMap.put(keySet, supportValue);
				}
			}

			//generating confidences:
			//get all rule possibilities in tempRuleList
			List<Rule> tempRuleList = new ArrayList<Rule>();
			Iterator<Entry<Set<String>, Integer>> iter = largeItemSupportMap.entrySet().iterator();
			while(iter.hasNext())
			{
				Entry<Set<String>, Integer> entry = iter.next();
				Set<String> key = (Set<String>)entry.getKey();
				Integer value = (Integer)entry.getValue();
				Iterator<Entry<Set<String>, Integer>> inneriter = largeItemSupportMap.entrySet().iterator();
				while(inneriter.hasNext())
				{
					Entry<Set<String>, Integer> innerEntry = inneriter.next();
					Set<String> innerKey = (Set<String>)innerEntry.getKey();
					Integer innerValue = (Integer)innerEntry.getValue();
					
					if(union(key, innerKey).size() != 0 && innerKey.size() == 1)
					{
						Rule rule = new Rule(transactionList);
						rule.setLhs(key);
						rule.setRhs(innerKey);
						rule.setLhsCount(value);
						rule.setRhsCount(innerValue);
						tempRuleList.add(rule);
					}
				}
			}
			
			//Removing rules that have rHS in LHS and has conf. < minConf.
			for(int i = 0; i < tempRuleList.size(); i ++)
			{
				Rule currentRule = tempRuleList.get(i);
				if(currentRule.getLhs().containsAll(currentRule.getRhs()))
				{
					tempRuleList.remove(currentRule);
					continue;
				}
				if(currentRule.computeAndGetConfidence() < minConfidence)
				{
					tempRuleList.remove(currentRule);
					continue;
				}
				ruleList.add(currentRule);
			}
			sortedSupportMap.putAll(largeItemSupportMap);
			
			Comparator<Rule> comparator = new Comparator<Rule>() {
				public int compare(Rule o1, Rule o2) {
					if(o1.confidence <= o2.confidence)
						return 1;
					return -1;
				}
			}; 

			Collections.sort(ruleList, comparator);
			
			File outputFile = new File("output.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
			bw.write("Large ItemSets (decreasing order of support) :\n\n");
			Iterator<Map.Entry<Set<String>, Integer>> finalItemSetIterator = sortedSupportMap.entrySet().iterator();
			while(finalItemSetIterator.hasNext())
			{
				Entry<Set<String>, Integer> entry = finalItemSetIterator.next();
				Set<String> key = entry.getKey();
				Integer value = entry.getValue();
				double support = (value / (double)transactionList.size()) * 100.0;
				bw.write(Arrays.toString(key.toArray()) + ", " + support + "%\n");
			}
			bw.write("\n\nHigh-Confidence Rules (decreasing order of confidence) with support(LHS union RHS) :\n\n");
			for(int i = 0; i < ruleList.size(); i ++)
			{
				bw.write(ruleList.get(i) + "\n");
			}
			bw.close();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static ArrayList<Set<String>> getSubsets(ArrayList<String> set)
	{

		ArrayList<Set<String>> subsetCollection = new ArrayList<Set<String>>();

		if (set.size() == 0) {
			subsetCollection.add(new HashSet<String>());
		} else {
			ArrayList<String> reducedSet = new ArrayList<String>();

			reducedSet.addAll(set);

			String first = reducedSet.remove(0);
			ArrayList<Set<String>> subsets = getSubsets(reducedSet);
			subsetCollection.addAll(subsets);

			subsets = getSubsets(reducedSet);

			for (Set<String> subset : subsets) {
				subset.add(first);
			}

			subsetCollection.addAll(subsets);
		}

		return subsetCollection;
	}
	
	public static <T>Set<T> union(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new HashSet<T>(setA);
	    tmp.addAll(setB);
	    return tmp;
	  }
	
	private static int getSupportCount(Set<String> set)
	{
		int count = 0;
		for(int i = 0; i < transactionList.size(); i ++)
		{
			if(transactionList.get(i).containsAll(set))
			{
				count ++;
			}
		}
		return count;
	}
}

class Rule
{
	Set<String> lhs;
	Set<String> rhs;
	int lhsCount;
	int rhsCount;
	double confidence;
	int support;
	List<List<String>> transactionList = new ArrayList<List<String>>();
	
	Rule(List<List<String>> tl)
	{
		transactionList = tl;
		lhs = new HashSet<String>();
		rhs = new HashSet<String>();
	}
	
	private int getSupportCount(Set<String> set)
	{
		int count = 0;
		for(int i = 0; i < transactionList.size(); i ++)
		{
			if(transactionList.get(i).containsAll(set))
			{
				count ++;
			}
		}
		return count;
	}
	
	public void setLhs(Set<String> lhs) {
		this.lhs = lhs;
	}
	
	public Set<String> getLhs() {
		return lhs;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(lhs.toArray()) + " ==> " + Arrays.toString(rhs.toArray()) + " (Conf: " + confidence * 100 + "%, Supp: " + (getSupportCount(union(lhs,rhs)) / (double)transactionList.size()) * 100 + "%)";
	}
	
	public int getLhsCount() {
		return lhsCount;
	}
	
	public Set<String> getRhs() {
		return rhs;
	}
	
	public int getRhsCount() {
		return rhsCount;
	}
	
	public void setLhsCount(int lhsSupport) {
		this.lhsCount = lhsSupport;
	}
	
	public void setRhs(Set<String> rhs) {
		this.rhs = rhs;
	}
	
	public void setRhsCount(int rhsSupport) {
		this.rhsCount = rhsSupport;
	}
	
	public double computeAndGetConfidence()
	{
		confidence = getSupportCount(union(lhs, rhs));
		support = (int)confidence;
		confidence /= getSupportCount(lhs);
		return confidence;
	}
	
	public <T>Set<T> union(Set<T> setA, Set<T> setB) {
	    Set<T> tmp = new HashSet<T>(setA);
	    tmp.addAll(setB);
	    return tmp;
	  }
}

class ValueComparator implements Comparator<Set<String>> {

    Map<Set<String>, Integer> base;
    public ValueComparator(Map<Set<String>, Integer> base) {
        this.base = base;
    }

    public int compare(Set<String> a, Set<String> b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        }
    }
}
