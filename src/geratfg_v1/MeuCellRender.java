/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geratfg_v1;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author usrlab25
 */
public class MeuCellRender extends DefaultTableCellRenderer {

    ArrayList<Integer> linhas;

    public MeuCellRender(ArrayList<Integer> linhas) {
        this.linhas = linhas;
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column) {
        Component result = super.getTableCellRendererComponent(
                table,
                value,
                isSelected,
                hasFocus,
                row,
                column);
        for (int i = 0; i < linhas.size(); i++) {
            if (row == linhas.get(i)) {

                setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.BLUE));
                return result;
            }
        }
        return result;
    }
}
