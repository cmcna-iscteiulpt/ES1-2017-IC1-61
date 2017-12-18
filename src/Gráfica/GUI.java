package Gráfica;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 * 
 * @author Ruben Beirao
 * @author Tiago Santos
 * @author Ben-Hur Fidalgo
 * 
 * @since 17-10-2017
 */

public class GUI {

	private JFrame frame = new JFrame("Projeto_ES1 - Grupo 61");
	private JPanel panelUp = new JPanel();
	private JPanel panelMedium = new JPanel();
	private JPanel panelDown = new JPanel();
	private JTextField textPathRules = new JTextField(
			"/Users/ben-hurfidalgo/Dropbox/ISCTE/IGE/3º ano/ES/Project/rules.cf");
	private JTextField textPathHam = new JTextField(
			"/Users/ben-hurfidalgo/Dropbox/ISCTE/IGE/3º ano/ES/Project/ham.log");
	private JTextField textPathSpam = new JTextField(
			"/Users/ben-hurfidalgo/Dropbox/ISCTE/IGE/3º ano/ES/Project/spam.log");
	String[] colunas = { "Regra", "Peso" };
	String[][] data = {};
	DefaultTableModel modelME = new DefaultTableModel(data, colunas);
	DefaultTableModel modelIN = new DefaultTableModel(data, colunas);
	ArrayList<String> regras = new ArrayList<String>();
	ArrayList<String> hamMessages = new ArrayList<String>();
	ArrayList<String> spamMessages = new ArrayList<String>();

	/**
	 * This method creates the GUI after running all methods
	 */

	public GUI() {
		frame.setLayout(new GridLayout(3, 1));
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		addFrameContent();
		// frame.setSize(620, 500);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 * This method adds all the contents that will appear in the frame
	 */

	private void addFrameContent() {
		// gerar painel superior
		panelUp = painelSuperior();
		// adicionar painel superior
		frame.add(panelUp);
		// gerar painel mediano
		panelMedium = generatePainelMed();
		// adicionar painel mediano
		frame.add(panelMedium);
		// gerar painel inferior
		panelDown = generatePainelInferior();
		// adicionar painel inferior
		frame.add(panelDown);
	}

	/**
	 * 
	 * @return
	 */
	private JPanel painelSuperior() {
		// labels estaticas
		JLabel rules_path = new JLabel("rules.cf path");
		JLabel ham_path = new JLabel("ham.log path");
		JLabel spam_path = new JLabel("spam.log path");
		JButton carregar_ficheiros = new JButton("Carregar ficheiros");

		// mini paineis para cada label + caminho (vazio)
		// linha do rules.cf
		JPanel panelRules = new JPanel(new GridLayout(1, 2));
		panelRules.add(rules_path);
		panelRules.add(textPathRules);

		// linha do ham.log
		JPanel panelHam = new JPanel(new GridLayout(1, 2));
		panelHam.add(ham_path);
		panelHam.add(textPathHam);

		// linha do spam.log
		JPanel panelSpam = new JPanel(new GridLayout(1, 2));
		panelSpam.add(spam_path);
		panelSpam.add(textPathSpam);

		// Bot�o para carregar os ficheiros
		carregar_ficheiros.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				readRulesFile();
				readHamFile();
				readSpamFile();
			}

		});

		// criar painelLocal a ser devolvido com 2 colunas e 3 linhas
		JPanel local = new JPanel();
		local.setLayout(new GridLayout(4, 1));
		local.add(panelRules);
		local.add(panelHam);
		local.add(panelSpam);
		local.add(carregar_ficheiros);

		return local;
	}

	/**
	 * 
	 * @return
	 */

	private JPanel generatePainelMed() {

		JPanel local = new JPanel(new GridLayout(1, 2));
		JPanel panelLeft = new JPanel(new BorderLayout());
		JPanel panelRight = new JPanel(new GridLayout(3, 1));
		JPanel panelFalsos = new JPanel();
		JTextField fpositive = new JTextField("FP");
		JTextField fnegative = new JTextField("FN");
		fpositive.setEditable(false);
		fnegative.setEditable(false);

		// pLeft
		JTable jTableManual = genTableManual();
		panelLeft.add(new JScrollPane(jTableManual), BorderLayout.CENTER);
		panelFalsos.add(fpositive);
		panelFalsos.add(fnegative);
		panelLeft.add(panelFalsos, BorderLayout.SOUTH);

		// pRight
		JButton button_auto_config = new JButton("Gerar uma configuracao automatica");
		button_auto_config.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (int i = 0; i < regras.size(); i++) {
					double p = ThreadLocalRandom.current().nextDouble(-5, 5);
					p = Math.round(p * 10) / 10D;
					modelME.setValueAt(p, i, 1);
				}
			}
		});
		JButton button_aval_calc = new JButton("Avaliar e calcular FP e FN");
		button_aval_calc.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int i;
				fpositive.setText(String.valueOf(calculateFalsePositives()));
				fnegative.setText(String.valueOf(calculateFalseNegatives()));
			}
		});
		JButton button_save_config = new JButton("Guardar a configuracao");
		button_save_config.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					saveConfiguration(data, textPathRules.getText());
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		panelRight.add(button_auto_config);
		panelRight.add(button_aval_calc);
		panelRight.add(button_save_config);

		// adicionar os paineis interiores ao painel medio
		local.add(panelLeft);
		local.add(panelRight);
		return local;
	}

	/**
	 * 
	 */

	private JTable genTableManual() {
		/*
		 * Then the Table is constructed using these data and columnNames: JTable table
		 * = new JTable(data, columnNames); There are two JTable constructors that
		 * directly accept data (SimpleTableDemo uses the first): JTable(Object[][]
		 * rowData, Object[] columnNames) JTable(Vector rowData, Vector columnNames)
		 */
		// String[] nomeColunas = { "Regra", "Peso" };
		// Object data[][] = { { "Regra1", "Peso1" }, { "Regra3", "Peso3" }, { "Regra3",
		// "Peso3" }, { "Regra3", "Peso3" },
		// { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, {
		// "Regra3", "Peso3" },
		// { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, {
		// "Regra3", "Peso3" },
		// { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, {
		// "Regra7", "Peso3" } };

		JTable local = new JTable(modelME);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	/**
	 * 
	 * @return
	 */

	private JPanel generatePainelInferior() {
		JPanel local = new JPanel(new GridLayout(1, 2));
		JPanel panelLeft = new JPanel(new BorderLayout());
		JPanel panelRight = new JPanel(new GridLayout(3, 1));
		JPanel panelFalsos = new JPanel();
		JTextField fpositive = new JTextField("FP: ");
		JTextField fnegative = new JTextField("FN: ");
		fpositive.setEditable(false);
		fnegative.setEditable(false);

		// pLeft
		JTable jTableManual = genTableAuto();
		panelLeft.add(new JScrollPane(jTableManual));
		panelFalsos.add(fpositive);
		panelFalsos.add(fnegative);
		panelLeft.add(panelFalsos, BorderLayout.SOUTH);

		// pRight
		JButton button_auto_config = new JButton("Gerar uma configuracao automatica");
		JButton button_save_config = new JButton("Guardar a configuracao");
		panelRight.add(button_auto_config);
		panelRight.add(button_save_config);

		// adicionar os paineis interiores ao painel medio
		local.add(panelLeft);
		local.add(panelRight);
		return local;
	}

	/**
	 * 
	 * @return
	 */

	private JTable genTableAuto() {
		// String[] nomeColunas = { "Regra", "Peso" };
		// Object data[][] = { { "Regra1", "Peso1" }, { "Regra3", "Peso3" }, { "Regra3",
		// "Peso3" }, { "Regra3", "Peso3" },
		// { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, {
		// "Regra3", "Peso3" },
		// { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, {
		// "Regra3", "Peso3" },
		// { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, { "Regra3", "Peso3" }, {
		// "Regra7", "Peso3" } };
		// JTable local = new JTable(data, nomeColunas);
		// local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		// return local;
		JTable local = new JTable(modelIN);
		local.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		return local;
	}

	/**
	 * 
	 */

	public void readRulesFile() {
		File file = new File(textPathRules.getText());

		if (file.isFile() && file.getName().endsWith(".cf")) {

			try {
				FileInputStream fstream = new FileInputStream(file);

				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					String strLine;
					String[] regra = new String[2];
					while ((strLine = br.readLine()) != null) {
						String[] rule = strLine.split("\t");
						regra[0] = rule[0];
						if (rule.length > 1 && rule[1] != null)
							regra[1] = rule[1];
						// Print the content on the console
						regras.add(regra[0]);
						modelME.addRow(regra);
						modelIN.addRow(regra);
					}
				}

			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 */

	public void readHamFile() {
		int numlines = 0;
		File file = new File(textPathHam.getText());
		if (file.isFile() && file.getName().endsWith(".log")) {
			try {
				FileInputStream fstream = new FileInputStream(file);

				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					int k = 0;
					String strLine = br.readLine();
					while (strLine != null) {
						hamMessages.add(strLine);
						strLine = br.readLine();
						k++;
					}
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	private void readSpamFile() {
		int numlines = 0;
		File file = new File(textPathSpam.getText());
		if (file.isFile() && file.getName().endsWith(".log")) {
			try {
				FileInputStream fstream = new FileInputStream(file);

				try (DataInputStream in = new DataInputStream(fstream)) {
					BufferedReader br = new BufferedReader(new InputStreamReader(in));
					int k = 0;
					String strLine = br.readLine();
					while (strLine != null) {
						spamMessages.add(strLine);
						strLine = br.readLine();
						k++;
					}
				}
			} catch (Exception e) {
				System.err.println("Error: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 * @return
	 */

	public int calculateFalsePositives() {
		// FALSE POS, hamm into spam
		int falsePositives = 0;
		double valor = 0;
		String[] linha;
		for (int i = 0; i < hamMessages.size(); i++) {
			valor = 0;
			linha = hamMessages.get(i).split("\t");
			// mensagem + REGRAS
			// 0028674f122eeb4cd901867d74f5676c85809 +BAYES_00+ ...
			for (int j = 1; j < linha.length; j++) {
				for (int k = 0; k < modelME.getRowCount(); k++) {
					if (modelME.getValueAt(k, 0).equals(linha[j])) {
						double aux;
						if (modelME.getValueAt(k, 1) instanceof String)
							aux = Double.parseDouble((String) modelME.getValueAt(k, 1));
						else
							aux = (double) modelME.getValueAt(k, 1);
						valor += aux;
					}
				}
			}
			if (valor > 5) {
				falsePositives++;
			}
		}
		return falsePositives;

	}

	public int calculateFalseNegatives() {
		// FALSE POS, hamm into spam
		int falseNegatives = 0;
		double valor = 0;
		String[] linha;
		for (int i = 0; i < spamMessages.size(); i++) {
			valor = 0;
			linha = spamMessages.get(i).split("\t");
			// mensagem + REGRAS
			// 0028674f122eeb4cd901867d74f5676c85809 +BAYES_00+ ...
			for (int j = 1; j < linha.length; j++) {
				for (int k = 0; k < modelME.getRowCount(); k++) {
					if (modelME.getValueAt(k, 0).equals(linha[j])) {
						//
						double aux;
						if (modelME.getValueAt(k, 1) instanceof String)
							aux = Double.parseDouble((String) modelME.getValueAt(k, 1));
						else
							aux = (double) modelME.getValueAt(k, 1);
						//
						valor += aux;
					}
				}
			}
			if (valor < 5) {
				falseNegatives++;
			}
		}
		return falseNegatives;

	}

	public void saveConfiguration(Object[][] data, String path)
			throws FileNotFoundException, UnsupportedEncodingException {
		PrintWriter pw = new PrintWriter(path, "UTF-8");
		/*
		 * for (int i = 0; i < data.length; i++) { pw.write(data[i][0] + "\t" +
		 * data[i][1] + "\n"); }
		 */
		for (int i = 0; i < modelME.getRowCount(); i++) {
			pw.write(modelME.getValueAt(i, 0) + "\t" + modelME.getValueAt(i, 1) + "\n");
		}
		pw.close();
	}

	public static void main(String[] args) {
		// main
		new GUI();
	}
}
