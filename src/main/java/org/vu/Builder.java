package org.vu;

public class Builder {
	public static void main(String[] args) {
		Builder builder = new Builder();
		builder.run();
	}

	private void run() {
		Configure configure = new Configure();
		configure.setup();
	}

}
