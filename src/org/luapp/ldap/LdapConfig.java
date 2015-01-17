package org.luapp.ldap;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by lumeng on 2015/1/17.
 */
public class LdapConfig extends JFrame implements ActionListener {

    private JButton btn_ok;
    private JButton btn_cancel;
    private JTextField ldapUrlTxt;
    private JTextField initDnTxt;
    private JTextField userTxt;
    private JTextField regexUserTxt;
    private JPanel rootPannel;
    private JPasswordField pwdTxt;

    public LdapConfig() {
        btn_ok.addActionListener(this);
        btn_cancel.addActionListener(this);

        setContentPane(rootPannel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("LDAP导入设置");
        pack();
        setSize(400, 200);
        // 禁用最大最小化按钮
        setResizable(false);
        // setLocationRelativeTo在setSize后面  设置为null表示居中显示
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        LdapConfig ldap = new LdapConfig();
        ldap.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (btn_ok.equals(e.getSource())) {
            try {
                checkFieldNotNull(ldapUrlTxt, "URL不能为空");
                checkFieldNotNull(initDnTxt, "入口DN不能为空");
                checkFieldNotNull(userTxt, "用户名不能为空");
                checkFieldNotNull(pwdTxt, "密码不能为空");
                checkFieldNotNull(regexUserTxt, "用户匹配模式不能为空");
            } catch (Exception ex) {
                return;
            }

            String url = ldapUrlTxt.getText().trim();
            String initDn = initDnTxt.getText().trim();
            String user = userTxt.getText().trim();
            String pwd = new String(pwdTxt.getPassword()).trim();
            String regexUser = regexUserTxt.getText().trim();

            this.setVisible(false);

            LdapViewer viewer = new LdapViewer(url, initDn, user, pwd, regexUser);
            viewer.setVisible(true);
        } else if (btn_cancel.equals(e.getSource())) {
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
