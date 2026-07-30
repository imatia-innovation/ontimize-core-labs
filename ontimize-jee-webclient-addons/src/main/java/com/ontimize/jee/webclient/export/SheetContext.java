package com.ontimize.jee.webclient.export;

/**
 * Contexto de hoja de calculo. Se envia cada vez que se exporta una fila a la callback donde el usuario decide si
 * necesita crear otra hoja nueva. Tiene datos como el indice en el libro de la hoja actual, para poder agregarle un
 * numero al nombre, el nombre original y el nombre de la hoja actual. Por ejemplo, el nombre original podria ser
 * "Resultados" y cada hoja "Resultados_1", Resultados_2" etc Se proporciona un comportamiento por defecto que crea
 * hojas nuevas cuando se llega al limite maximo de filas de Excel (1.048.576)
 *
 * @author antonio.vazquez@imatia.com Antonio Vazquez Araujo
 */

public class SheetContext {

    // Fila del model
    private final int row;

	// Numero de filas de esta hoja
    private final int numRows;

    // Nombre inicial
    private final String firstSheetName;

    // Nombre de la hoja actual
    private final String actualSheetName;

	// indice de esta hoja en el libro
    private final int actualSheetIndex;

    public SheetContext(final int row, final int numRows, final String firstSheetName, final String actualSheetName,
            final int actualSheetIndex) {
        this.row = row;
        this.numRows = numRows;
        this.firstSheetName = firstSheetName;
        this.actualSheetName = actualSheetName;
        this.actualSheetIndex = actualSheetIndex;
    }

    public String getFirstSheetName() {
        return this.firstSheetName;
    }

    public String getActualSheetName() {
        return this.actualSheetName;
    }

    public int getRow() {
        return this.row;
    }

    public int getNumRows() {
        return this.numRows;
    }

    public int getActualSheetIndex() {
        return this.actualSheetIndex;
    }

}
