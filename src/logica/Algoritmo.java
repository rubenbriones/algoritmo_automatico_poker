package logica;

public abstract class Algoritmo implements IAlgoritmo {
	
	protected ISelectorManos selector;
	protected ICalculadoraEquity calc;

	public Algoritmo(ISelectorManos selector, ICalculadoraEquity calc) {
		this.selector = selector;
		this.calc = calc;
	}

	public ISelectorManos getSelector() {
		return selector;
	}

	public void setSelector(ISelectorManos selector) {
		this.selector = selector;
	}

	public ICalculadoraEquity getCalc() {
		return calc;
	}

	public void setCalc(ICalculadoraEquity calc) {
		this.calc = calc;
	}
	
}
