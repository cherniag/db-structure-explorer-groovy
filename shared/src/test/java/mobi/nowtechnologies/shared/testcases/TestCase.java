package mobi.nowtechnologies.shared.testcases;

import java.util.Arrays;

public class TestCase<IN, OUT> {
	private String method = "DEFAULT METHOD";
	private int retries = 1;

	private IN input;
	private OUT[] output;

	private int hash;

	public TestCase(String method, int retries, IN input, OUT... output)
	{
		this.method = method != null ? method : this.method;
		this.retries = retries > 0 ? retries : this.retries;
		this.input = input;
		this.output = output != null && output.length != 0 ? Arrays.copyOf(output, retries) : null;
	}

	public TestCase(int retries, IN input, OUT... output)
	{
		this(null, retries, input, output);
	}

	@SuppressWarnings("unchecked")
	public TestCase(IN input, OUT output)
	{
		this(null, 0, input, output);
	}

	@Override
	public int hashCode() {
		if (hash == 0) {
			hash = 1;

			hash = (31 * hash) + method.hashCode();
			if (input != null)
			{
				if (input instanceof Object[]) {
					Object[] inputarr = (Object[]) input;
					for (int i = 0; i < inputarr.length; i++) {
						hash = (31 * hash) + inputarr[i].hashCode();
					}
				} else {
					hash = (31 * hash) + input.hashCode();
				}
			} else {
				hash = (31 * hash) + 0;
			}
		}

		return hash;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object o) {
		if(this == o)
			return true;
		if(o == null)
			return false;
		if(o.getClass() != this.getClass())
			return false;
		
		TestCase<IN, OUT> testcase = (TestCase<IN, OUT>)o;
		
		return testcase.hashCode() == this.hashCode();
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public int getRetries() {
		return retries;
	}

	public void setRetries(int retries) {
		this.retries = retries;
	}

	public IN getInput() {
		return input;
	}

	public OUT[] getOutput() {
		return output;
	}

	public OUT getOutput(int retry) {
		return output != null && output.length > retry && retry >= 0 ? output[retry] : null;
	}
}
