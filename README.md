# algoritmo_automatico_poker
Este proyecto se basa en el desarrollo de una aplicación que sea capaz de jugar al póker automáticamente en la modalidad No-Limit Texas Hold'em, donde hay del orden de 10^160 posibles situaciones (en el ajedrez hay 10^120). El objetivo del proyecto no será que el algoritmo simplemente sepa jugar, sino que además lo haga de forma inteligente, usando una estrategia que permita explotar los errores de sus rivales. Además, se mostrarán de manera gráfica las estimaciones y razonamientos del algoritmo para que el usuario pueda revisarlos y entenderlos.
La implementación del sistema se ha llevado a cabo en Java, y para la interfaz del mismo hemos utilizado JavaFX.
El algoritmo ha sido testeado jugando contra otro robot automático de póker llamado Clever Piggy. Debido a la ausencia de una API de este robot, hemos tenido que leer los datos de su web utilizando técnicas de scraping.

Para el desarrollo del proyecto hice uso de varios patrones de diseño: Observador, Estrategia y Singleton, como se puede observar en el siguiente diagrama de clases. El proyecto consta de más de 100 clases y unas 8000 líneas de código.

![Diagrama de clases completo](https://github.com/rubenbriones/algoritmo_automatico_poker/blob/master/Diagrama%20de%20clases.png)

Interfaz gráfica del programa:

![Interfaz gráfica](https://github.com/rubenbriones/algoritmo_automatico_poker/blob/master/Interfaz%20grafica.png)

Enlace a la memoria comlpeta del TFG: <https://riunet.upv.es/handle/10251/86512>
