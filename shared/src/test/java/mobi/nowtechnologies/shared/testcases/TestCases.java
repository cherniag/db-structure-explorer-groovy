package mobi.nowtechnologies.shared.testcases;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

public class TestCases<IN, OUT> {
	private Map<TestCase<IN, OUT>, OUT[]> map = new HashMap<TestCase<IN, OUT>, OUT[]>();

	public void add(TestCase<IN, OUT> testcase){
		map.put(testcase, testcase.getOutput());
	}
	
	public Set<TestCase<IN, OUT>> getAll(){
		return map.keySet();
	}
	
	public Set<TestCase<IN, OUT>> getAll(String method){
		Set<TestCase<IN, OUT>> set = new HashSet<TestCase<IN,OUT>>();
		
		for (TestCase<IN, OUT> testcase : map.keySet()) {
			if(StringUtils.equals(testcase.getMethod(), method))
				set.add(testcase);
		}
		
		return set;
	}

	public OUT[] get(IN input){
		return map.get(new TestCase<IN, OUT>(input, null));
	}

	public OUT get(IN input, int retry){
		OUT[] output =  map.get(new TestCase<IN, OUT>(input, null));
		return output != null && retry < output.length && retry >= 0 ? output[retry] : null;
	}
	
	public OUT[] get(String method, IN input){
		return map.get(new TestCase<IN, OUT>(method, 0, input, (OUT[])null));
	}

	public OUT get(String method, IN input, int retry){
		OUT[] output =  map.get(new TestCase<IN, OUT>(method, 0, input, (OUT[])null));
		return output != null && retry < output.length && retry >= 0 ? output[retry] : null;
	}
	
	public void clear()
	{
		map.clear();
	}
}