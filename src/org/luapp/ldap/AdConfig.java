package org.luapp.ldap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by lumeng on 2015/1/20.
 */
public class AdConfig  extends JFrame implements ActionListener {

    private JPanel rootPanel;
    private JTextField ldapUrlTxt;
    private JTextField baseDnTxt;
    private JTextField domainTxt;
    private JTextField userNameTxt;
    private JButton btnOk;
    private JButton btnCancel;
    private JPasswordField pwdTxt;

    public AdConfig() {
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);

        setContentPane(rootPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("AD导入设置");
        pack();
        setSize(400, 200);
        // 禁用最大最小化按钮
        setResizable(false);
        // setLocationRelativeTo在setSize后面  设置为null表示居中显示
        setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (btnOk.equals(e.getSource())) {
            try {
                checkFieldNotNull(ldapUrlTxt, "URL不能为空");
                checkFieldNotNull(baseDnTxt, "入口DN不能为空");
                checkFieldNotNull(domainTxt, "domain不能为空");
                checkFieldNotNull(userNameTxt, "用户名不能为空");
                checkFieldNotNull(pwdTxt, "密码不能为空");
            } catch (Exception ex) {
                return;
            }

            String ldapUrl = ldapUrlTxt.getText().trim();
            String baseDn = baseDnTxt.getText().trim();
            String domain = domainTxt.getText().trim();
            String user = userNameTxt.getText().trim();
            String pwd = new String(pwdTxt.getPassword()).trim();

            this.setVisible(false);

            AdViewer viewer = new AdViewer(ldapUrl, baseDn, domain,user, pwd);
            viewer.setVisible(true);
        } else if (btnCancel.equals(e.getSource())) {
            System.out.println("Cancel clicked");
            System.exit(0);
        }
    }

    private void checkFieldNotNull(JTextField field, String message) {
        String txt = field.getText();
        if (txt == null || txt.equals("")) {
            JOptionPane.showMessageDialog(null, message, "输入错误", JOptionPane.WARNING_MESSAGE);
            field.grabFocus();
            throw new IllegalArgumentException();
        }
    }
}
