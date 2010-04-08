package com.puzzletimer.scrambles;

public enum PyraminxMove implements Move {
	U("U"), U2("U'"), u("u"), u2("u'"),
	L("L"), L2("L'"), l("l"), l2("l'"),
	R("R"), R2("R'"), r("r"), r2("r'"),
	B("B"), B2("B'"), b("b"), b2("b'");	
	
	private String description;
	
	private PyraminxMove(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}	
}
