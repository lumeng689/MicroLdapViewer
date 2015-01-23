package org.luapp.ldap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by lumeng on 2015/1/20.
 */
public class TypeSelector extends JFrame implements ActionListener {
    private JButton btnAD;
    private JButton btnLdap;
    private JPanel rootPanel;

    public TypeSelector() {
        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LDAP测试工具");
        pack();
        setSize(400, 200);
        // 禁用最大最小化按钮
        setResizable(false);
        // setLocationRelativeTo在setSize后面  设置为null表示居中显示
        setLocationRelativeTo(null);

        btnAD.addActionListener(this);
        btnLdap.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (btnAD.equals(e.getSource())) {
            System.out.println("ad setup");
            AdConfig config = new AdConfig();
            config.setVisible(true);
            this.setVisible(false);
        } else if (btnLdap.equals(e.getSource())) {
            System.out.println("ldap setup");
            LdapConfig config = new LdapConfig();
            config.setVisible(true);
            this.setVisible(false);
        }
    }
}
