
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.CompoundBorder;
import javax.swing.text.*;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.lang.Object;
import java.util.*;

/**
 * Created by thanasis on 5/5/2016.
 */

public class MainGUI{

    private JFrame mainFrame;

    private JPanel UserInputPanel,ConsolePanel,RightPanel;
    private JTextField textboxRows,textboxCols,textBoxMaxIter,textBoxCCvariation,textBoxRandomLevel;
    private JLabel Title;
    private ButtonGroup Items;

    private MyDrawPanel ColorBox;

    private DARPPane DarpResult;

    private Color CurrColor = Color.MAGENTA;

    private JRadioButton ObstaclesButton;
    private JRadioButton EmptyButton;
    private JRadioButton RobotButton;
    private JButton RepaintDARP;
    private JTextPane consoleToPrint;

    private GridPane ColorGrid;

    private double estimatedTime;
    private int [][] EnvironmentGrid;

    private JPanel SuperRadio;
    private FinalPaths mCPPResult;

    private Color[] ColorsNr;
    private int nr, EffectiveSize, maxCellsRobot, minCellsRobot,rows,cols, obs, CurrentIDXAdd, CurrentCompDisp;

    private boolean retainData;

    private JCheckBox checkBoxMSTs;




    MainGUI(){
        mainFrame = new JFrame("Divide Areas Algorithm For Optimal " +
                "Multi-Robot Coverage Path Planning");
        UserInputPanel = new JPanel();
        UserInputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        textboxRows = new JTextField(4);
        textboxRows.setDocument(new JTextFieldLimit(2));
        textboxRows.setText("15");
        textboxCols = new JTextField(4);
        textboxCols.setDocument(new JTextFieldLimit(2));
        textboxCols.setText("15");
        textBoxMaxIter = new JTextField(6);
        textBoxMaxIter.setDocument(new JTextFieldLimit(6));
        textBoxMaxIter.setText("3000");
        textBoxCCvariation = new JTextField(6);
        textBoxCCvariation.setDocument(new JTextFieldLimit(6));
        textBoxCCvariation.setText("0.01");
        textBoxRandomLevel = new JTextField(6);
        textBoxRandomLevel.setDocument(new JTextFieldLimit(6));
        textBoxRandomLevel.setText("0.0001");
        ObstaclesButton = new JRadioButton("Obstacle");
        ObstaclesButton.setBackground(Color.white);
        EmptyButton = new JRadioButton("Unoccupied Cell");
        EmptyButton.setBackground(Color.white);
        RobotButton = new JRadioButton("Robot");
        RobotButton.setBackground(Color.white);
        CurrentIDXAdd=-1;
        retainData=false;
        DarpResult = null;
        ColorGrid = null;

        DefineRightPanel();
        mainFrame.getContentPane().add(BorderLayout.EAST,RightPanel);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.setLocationByPlatform(true);
        mainFrame.setSize(1100,800);
        mainFrame.setVisible(true);
    }


    private void DefineRightPanel()
    {
        RightPanel = new JPanel();
        //RightPanel.setLayout(new GridLayout(2,1));
        RightPanel.setPreferredSize(new Dimension(270,800));
        RightPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        DefineGridDimensions();
        DefineConsole();
        gbc.gridx = gbc.gridy = 0;
        gbc.gridwidth = gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.weightx = gbc.weighty = 70;
        RightPanel.add(UserInputPanel,gbc);

        gbc.gridy = 1;
        gbc.weightx = gbc.weighty = 30;
        gbc.insets = new Insets(2, 2, 2, 2);
        RightPanel.add(ConsolePanel,gbc);
    }


    private void DefineGridDimensions() {
        UserInputPanel.setBackground(Color.white);

        Title = new JLabel("Operational Environment Size");
        Title.setFont(new Font("serif", Font.BOLD, 18));

        JLabel textRows = new JLabel("#Rows:  2x");

        JLabel textCols = new JLabel("#Cols:  2x");

        JButton submitRowsCols = new JButton("Submit");
        submitRowsCols.addActionListener(new submitRowsColsListener());

        JPanel RowsPane = new JPanel();
        RowsPane.setBackground(Color.white);
        RowsPane.add(textRows);
        RowsPane.add(textboxRows);
        JPanel ColsPane = new JPanel();
        ColsPane.setBackground(Color.white);
        ColsPane.add(textCols);
        ColsPane.add(textboxCols);


        UserInputPanel.add(Title);
        UserInputPanel.add(RowsPane);
        UserInputPanel.add(ColsPane);
        UserInputPanel.add(submitRowsCols);
    }

    private void DefineConsole() {
        ConsolePanel = new JPanel();
        ConsolePanel.setBackground(Color.white);
        ConsolePanel.setLayout(new BoxLayout(ConsolePanel, BoxLayout.Y_AXIS));
        consoleToPrint = new JTextPane();


        JLabel ConsoleTitle = new JLabel("Console");
        ConsoleTitle.setFont(new Font("serif", Font.BOLD, 18));
        ConsolePanel.add(ConsoleTitle);
        ConsolePanel.add(consoleToPrint);

        consoleToPrint.setBackground(Color.black);
        consoleToPrint.setEditable(false);

        JScrollPane scrollConsole = new JScrollPane(consoleToPrint,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        ConsolePanel.add(scrollConsole);
    }


    private void DefineRobotsObstacles() {
        UserInputPanel.removeAll();
        UserInputPanel.setBackground(Color.white);

        if (!retainData) {
            EnvironmentGrid = new int[rows][cols];
            ColorGrid = new GridPane();
        }
        mainFrame.getContentPane().add(BorderLayout.CENTER,ColorGrid);

        Title = new JLabel("Obstacles - Robots Locations");
        Title.setFont(new Font("serif", Font.BOLD, 18));


        JPanel RadioAreaButtons = new JPanel();
        RadioAreaButtons.setBackground(Color.WHITE);
        Items = new ButtonGroup();
        Items.add(ObstaclesButton);
        Items.add(EmptyButton);
        Items.add(RobotButton);

        //Default add obstacle
        ObstaclesButton.setSelected(true);
        CurrentIDXAdd = 1;
        CurrColor = Color.BLACK;
        appendToPane("Click to add an obstacle\n\n", Color.WHITE);
        //Items.clearSelection();

        RadioAreaButtons.setLayout(new BoxLayout(RadioAreaButtons, BoxLayout.Y_AXIS));
        RadioAreaButtons.add(ObstaclesButton,BorderLayout.WEST);
        RadioAreaButtons.add(EmptyButton,BorderLayout.WEST);
        RadioAreaButtons.add(RobotButton,BorderLayout.WEST);

        JButton ResetCells = new JButton("Reset all cells");
        ResetCells.addActionListener(new ResetCellsClass());

        SuperRadio = new JPanel();
        SuperRadio.setPreferredSize(new Dimension(265,135));
        SuperRadio.setBackground(Color.WHITE);
        SuperRadio.add(RadioAreaButtons);
        ColorBox = new MyDrawPanel();
        SuperRadio.add(ColorBox);
        SuperRadio.add(ResetCells);
        SuperRadio.setBorder(BorderFactory.createTitledBorder("Elements"));

        ObstaclesButton.addActionListener(new CurrentComponentToAdd());
        EmptyButton.addActionListener(new CurrentComponentToAdd());
        RobotButton.addActionListener(new CurrentComponentToAdd());

        JPanel DARP = new JPanel();
        DARP.setPreferredSize(new Dimension(265,150));
        JButton startExp = new JButton("RUN!");
        startExp.addActionListener(new StartDARP());
        JLabel MaxIterLabel = new JLabel("#Maximum Iterations: ");
        DARP.setBackground(Color.white);

        JLabel CCvariation = new JLabel("Connected Component: ");
        JLabel RandomLevelLabel = new JLabel("%Random Influence: ");


        DARP.add(MaxIterLabel);
        DARP.add(textBoxMaxIter);
        DARP.add(CCvariation);
        DARP.add(textBoxCCvariation);
        DARP.add(RandomLevelLabel);
        DARP.add(textBoxRandomLevel);
        DARP.add(startExp);
        DARP.setBorder(BorderFactory.createTitledBorder("Find the coverage paths"));

        JButton ReturnButton = new JButton("Return");
        ReturnButton.addActionListener(new ReturnToTheInitialGUI());

        JLabel Title2 = new JLabel("DARP");
        Title2.setFont(new Font("serif", Font.BOLD, 18));

        UserInputPanel.add(ReturnButton);
        UserInputPanel.add(Title);
        UserInputPanel.add(SuperRadio);
        UserInputPanel.add(Title2);
        UserInputPanel.add(DARP);

        retainData=false;
    }


    private void DisplayFinalPanel() {
        UserInputPanel.removeAll();
        CurrentCompDisp=2;
        UserInputPanel.setBackground(Color.white);
        Title = new JLabel("Evaluate Results");
        Title.setFont(new Font("serif", Font.BOLD, 18));

        JButton ReturnButton = new JButton("Clear All");
        ReturnButton.addActionListener(new ReturnToTheInitialGUI());


        JButton SoftReturnButton = new JButton("Edit the current configuration");
        SoftReturnButton.addActionListener(new SoftReturnAction());

        JPanel RadioAreaButtons = new JPanel();
        RadioAreaButtons.setBackground(Color.WHITE);

        Items = new ButtonGroup();


        JRadioButton InitialPanelChoice = new JRadioButton("Input Environment");
        InitialPanelChoice.setBackground(Color.white);
        Items.add(InitialPanelChoice);


        JRadioButton DarpChoice = new JRadioButton("Area division (DARP)");
        DarpChoice.setBackground(Color.white);
        Items.add(DarpChoice);

        JRadioButton PathsChoise = new JRadioButton("Coverage Paths");
        PathsChoise.setBackground(Color.white);
        Items.add(PathsChoise);

        PathsChoise.setSelected(true);

        RadioAreaButtons.setLayout(new BoxLayout(RadioAreaButtons, BoxLayout.Y_AXIS));
        RadioAreaButtons.add(InitialPanelChoice,BorderLayout.WEST);
        RadioAreaButtons.add(DarpChoice,BorderLayout.WEST);
        RadioAreaButtons.add(PathsChoise,BorderLayout.WEST);


        RepaintDARP = new JButton("Re-Paint!");
        RepaintDARP.addActionListener(new RepaintDARPclasss());
        RepaintDARP.setEnabled(true);

        checkBoxMSTs = new JCheckBox("Display MSTs");
        //checkBoxMSTs.addActionListener(new CheckBoxActionClass());
        //checkBoxMSTs.setEnabled(true);

        SuperRadio = new JPanel();
        //SuperRadio.setLayout(new BoxLayout(SuperRadio, BoxLayout.Y_AXIS));
        SuperRadio.setPreferredSize(new Dimension(265,110));
        SuperRadio.setBackground(Color.WHITE);
        SuperRadio.add(RadioAreaButtons);
        SuperRadio.add(RepaintDARP);
        //SuperRadio.add(checkBoxMSTs);
        SuperRadio.setBorder(BorderFactory.createTitledBorder("Display Options"));

        InitialPanelChoice.addActionListener(new CurrentComponentToDisplay());
        DarpChoice.addActionListener(new CurrentComponentToDisplay());
        PathsChoise.addActionListener(new CurrentComponentToDisplay());


        JLabel statsLabel = new JLabel("Statistics");
        statsLabel.setFont(new Font("serif", Font.BOLD, 19));
        JPanel superStats = new JPanel();
        superStats.setBackground(Color.WHITE);
        superStats.setLayout(new BoxLayout(superStats, BoxLayout.Y_AXIS));
        superStats.add(new JLabel("Total number of cells to be covered: "+EffectiveSize));
        superStats.add(new JLabel("Robots: "+nr));
        superStats.add(new JLabel(String.format("Obstacles: %d (%.2f%% of the terrain)", 4*obs, 100.0*(double)obs/((double)rows*(double)cols))));
        superStats.add(new JSeparator());
        superStats.add(new JLabel("Maximum path length: "+maxCellsRobot+" cells"));
        superStats.add(new JLabel("Minimum path length: "+minCellsRobot+" cells"));
        superStats.add(new JLabel(String.format("Time required to compute paths: %.4f (sec)",estimatedTime)));

        UserInputPanel.add(ReturnButton);
        UserInputPanel.add(SoftReturnButton);
        UserInputPanel.add(Title);
        UserInputPanel.add(SuperRadio);
        UserInputPanel.add(statsLabel);
        UserInputPanel.add(superStats);

    }



    private void appendToPane(String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, c);

        aset = sc.addAttribute(aset, StyleConstants.FontFamily, "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);
        consoleToPrint.setEditable(true);
        int len = consoleToPrint.getDocument().getLength();
        consoleToPrint.setCaretPosition(len);
        consoleToPrint.setCharacterAttributes(aset, false);
        consoleToPrint.replaceSelection(msg);
        consoleToPrint.setEditable(false);
    }

    private void enableComponents(Container container, boolean enable) {
        Component[] components = container.getComponents();
        for (Component component : components) {
            component.setEnabled(enable);
            if (component instanceof Container) {
                enableComponents((Container)component, enable);
            }
        }
    }


    private class JTextFieldLimit extends PlainDocument {
        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
            if (str == null) return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }


    private class ResetCellsClass implements  ActionListener{
        public void actionPerformed(ActionEvent event){
            Object[] options = {"Yes", "Cancel"};

            int n = JOptionPane.showOptionDialog(mainFrame,
                    "Any Robot/Obstacles initialization progress will be lost.\n Do you really want to continue?",
                    "WARNING",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[1]); //default button title


            if (n == 0) {
                mainFrame.remove(ColorGrid);
                EnvironmentGrid = new int[rows][cols];
                ColorGrid = new GridPane();
                mainFrame.getContentPane().add(BorderLayout.CENTER, ColorGrid);
                mainFrame.setVisible(true);
                mainFrame.repaint();
            }
        }
    }


    private class CheckBoxActionClass implements ActionListener{
        public void actionPerformed(ActionEvent event){
            AbstractButton abstractButton = (AbstractButton) event.getSource();
            mCPPResult.setDispMST(abstractButton.getModel().isSelected());
            mCPPResult.paint();
            mainFrame.setVisible(true);
            mainFrame.repaint();
        }
    }

    private class RepaintDARPclasss implements ActionListener{
        public void actionPerformed(ActionEvent event){
            for (int r=0;r<nr;r++){ColorsNr[r]=generateRandomColor(null);}
            DarpResult.paint();
            mCPPResult.paint();
            mainFrame.setVisible(true);
            mainFrame.repaint();
        }
    }


    private class ReturnToTheInitialGUI implements ActionListener{
        public void actionPerformed(ActionEvent event){
            Object[] options = {"Yes, I want to start over", "Cancel"};

            String msgToDISP;
            if (event.getActionCommand().equals("Clear All")) {msgToDISP="All the data will be discarded." +
                    "\n Do you really want to continue?";}
            else {msgToDISP="Any Robot/Obstacles initialization progress will be lost." +
                    "\n Do you really want to continue?";}

            int n = JOptionPane.showOptionDialog(mainFrame,
                    msgToDISP,
                    "WARNING",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[1]); //default button title

            if (n == 0) {
                UserInputPanel.removeAll();
                EnvironmentGrid = null;
                CurrColor = Color.MAGENTA;
                CurrentIDXAdd = -1;
                rows = 0;
                cols = 0;
                if (DarpResult!=null) {mainFrame.remove(DarpResult);}
                if (ColorGrid!=null) {mainFrame.remove(ColorGrid);}
                if (mCPPResult!=null) {mainFrame.remove(mCPPResult);}
                DefineGridDimensions();
                appendToPane("Reinitialize the environment's size\n\n", Color.WHITE);
                ColorGrid = null;
                DarpResult=null;
                mCPPResult=null;
                mainFrame.setVisible(true);
                mainFrame.repaint();
            }
        }
    }



    private class SoftReturnAction implements ActionListener{
        public void actionPerformed(ActionEvent event){
            Object[] options = {"Yes, make the grid editable again", "Cancel"};

            int n = JOptionPane.showOptionDialog(mainFrame,
                    "The calculated paths are going to be discarded" +
                            "\n Do you really want to continue?",
                    "WARNING",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[1]); //default button title

            if (n == 0) {
                retainData=true;
                ColorGrid.enable =true;
                mainFrame.remove(DarpResult);
                mainFrame.remove(mCPPResult);
                appendToPane("Ready to perform any changes on the current configuration\n\n", Color.WHITE);
                DefineRobotsObstacles();
                DarpResult=null;
                mCPPResult=null;
                mainFrame.setVisible(true);
                mainFrame.repaint();
            }
        }
    }

    private class CurrentComponentToAdd implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (event.getActionCommand().equals("Robot") && CurrentIDXAdd != 2) {
                CurrColor = Color.BLUE;
                CurrentIDXAdd = 2;
                appendToPane("Click to add a robot\n\n", Color.WHITE);
            } else if (event.getActionCommand().equals("Obstacle") && CurrentIDXAdd != 1) {
                CurrColor = Color.BLACK;
                CurrentIDXAdd = 1;
                appendToPane("Click to add an obstacle\n\n", Color.WHITE);
            } else if (event.getActionCommand().equals("Unoccupied Cell") && CurrentIDXAdd != 0) {
                CurrColor = Color.WHITE;
                CurrentIDXAdd = 0;
                appendToPane("Click to reset to an unoccupied cell\n\n", Color.WHITE);
            }
            mainFrame.repaint();
        }

    }


    private class CurrentComponentToDisplay implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (event.getActionCommand().equals("Area division (DARP)") && CurrentCompDisp != 1) {
                if (CurrentCompDisp==0)
                    mainFrame.remove(ColorGrid);
                else
                    mainFrame.remove(mCPPResult);

                mainFrame.getContentPane().add(BorderLayout.CENTER,DarpResult);
                CurrentCompDisp=1;
                RepaintDARP.setEnabled(true);
                checkBoxMSTs.setEnabled(false);
            } else if (event.getActionCommand().equals("Input Environment") && CurrentCompDisp != 0) {
                if (CurrentCompDisp==1)
                    mainFrame.remove(DarpResult);
                else
                    mainFrame.remove(mCPPResult);

                mainFrame.getContentPane().add(BorderLayout.CENTER,ColorGrid);
                CurrentCompDisp = 0;
                RepaintDARP.setEnabled(false);
                checkBoxMSTs.setEnabled(false);
            } else if (event.getActionCommand().equals("Coverage Paths") && CurrentCompDisp != 2) {
                if (CurrentCompDisp==0)
                    mainFrame.remove(ColorGrid);
                else
                    mainFrame.remove(DarpResult);

                mainFrame.getContentPane().add(BorderLayout.CENTER,mCPPResult);
                CurrentCompDisp = 2;
                RepaintDARP.setEnabled(true);
                checkBoxMSTs.setEnabled(true);
            }
            mainFrame.setVisible(true);
            mainFrame.repaint();
        }

    }


    private class submitRowsColsListener implements ActionListener{
        public void actionPerformed(ActionEvent event){
            if (isNumeric(textboxRows.getText()) && isNumeric(textboxCols.getText()) && !textboxRows.getText().equals("0")
                    && !textboxCols.getText().equals("0")) {
                rows = Integer.parseInt(textboxRows.getText());
                cols = Integer.parseInt(textboxCols.getText());
                appendToPane("The grid [" + rows+","+cols+"] has been created\n\n",Color.WHITE);
                appendToPane("Define the Robots initial positions along with the fixed obstacles\n\n",Color.WHITE);
                DefineRobotsObstacles();
            }
            else {appendToPane("Please insert positive integer values\n\n",Color.WHITE);}

            mainFrame.setVisible(true);
            mainFrame.repaint();
        }

        private boolean isNumeric(String s) {
            return s.matches("[-+]?\\d*\\.?\\d+");
        }
    }

    private class StartDARP implements ActionListener{
        public void actionPerformed(ActionEvent event){

            if (!isNumeric(textBoxMaxIter.getText())){
                appendToPane("The maximum number of iterations is not a positive integer\n\n", Color.WHITE);
                return;
            }

            if (!isNumber(textBoxCCvariation.getText())){
                appendToPane("The connected components calibrator should be a valid double number\n\n", Color.WHITE);
                return;
            }

            if (!isNumber(textBoxRandomLevel.getText())){
                appendToPane("The random level should be a valid double number\n\n", Color.WHITE);
                return;
            }

            int MaxIter = Integer.parseInt(textBoxMaxIter.getText());
            double CCvariation = Double.parseDouble(textBoxCCvariation.getText());
            double randomLevel = Double.parseDouble(textBoxRandomLevel.getText());

            DARP problem = new DARP(rows, cols, EnvironmentGrid, MaxIter,CCvariation, randomLevel);

            if (problem.getNr()<=0) {
                appendToPane("Please define at least one robot (blue cell)\n\n", Color.WHITE);
                return;
            }

            enableComponents(UserInputPanel,false);
            ColorGrid.enable =false;
            //appendToPane("Interface is now locked\n\n", Color.WHITE);

            nr = problem.getNr();
            obs = problem.getNumOB();
            appendToPane("Framework with "+ nr +" robots and "+ obs+" obstacles has " +
                    "been received\n\n", Color.WHITE);

            appendToPane("Starting DARP algorithm...\n\n", Color.WHITE);

            problem.constructAssignmentM();

            if (problem.getSuccess()){
                int [][] A = problem.getAssignmentMatrix();
                appendToPane("DARP found an optimal space division\n\n", Color.WHITE);

                ColorsNr = new Color[nr];
                for (int r=0;r<nr;r++){ColorsNr[r]=generateRandomColor(null);}


                DarpResult = new DARPPane(A,nr,problem.getRobotBinary());
                DarpResult.paint();
                enableComponents(UserInputPanel,true);
                ArrayList<Vector> KruskalMSTS = calculateMSTs(problem.getBinrayRobotRegions(), nr);
                EffectiveSize = problem.getEffectiveSize();
                maxCellsRobot =problem.getMaxCellsAss();
                minCellsRobot = problem.getMinCellsAss();
                estimatedTime = problem.getElapsedTime();


                DisplayFinalPanel();

                ArrayList<Integer[]> InitRobots = problem.getRobotsInit();

                ArrayList<ArrayList<Integer[]>> AllRealPaths = new ArrayList<>();
                for (int r=0;r<nr;r++) {
                    CalculateTrajectories ct = new CalculateTrajectories(rows,cols, KruskalMSTS.get(r)); //Send MSTs
                    ct.initializeGraph(CalcRealBinaryReg(problem.getBinrayRobotRegions().get(r)), true); //Send [x2 x2] Binary Robot Region
                    ct.RemoveTheAppropriateEdges();
                    ct.CalculatePathsSequence(4*InitRobots.get(r)[0]*cols+2*InitRobots.get(r)[1]);
                    AllRealPaths.add(ct.getPathSequence());
                }

                mCPPResult = new FinalPaths(KruskalMSTS,A, nr, problem.getRobotBinary(), AllRealPaths, false);
                mCPPResult.paint();

                mainFrame.remove(ColorGrid);
                mainFrame.getContentPane().add(BorderLayout.CENTER,mCPPResult);
                mainFrame.setVisible(true);
                mainFrame.repaint();
            }else {
                appendToPane("The algorithm was terminated after "+MaxIter+" iterations, without finding " +
                        "an optimal cells assignment\n\n", Color.WHITE);
                enableComponents(UserInputPanel,true);
                ColorGrid.enable = true;
            }

        }


        private boolean[][] CalcRealBinaryReg(boolean[][] BinrayRobotRegion){
            boolean[][] RealBinrayRobotRegion = new boolean[2*rows][2*cols];
            for (int i=0;i<2*rows;i++){
                for (int j=0;j<2*cols;j++){
                    RealBinrayRobotRegion[i][j] = BinrayRobotRegion[i/2][j/2];
                }
            }
            return RealBinrayRobotRegion;
        }

        private ArrayList<Vector> calculateMSTs(ArrayList<boolean[][]> BinrayRobotRegions, int nr){
            ArrayList<Vector> MSTs = new ArrayList<>();
            for (int r=0;r<nr;r++){
                Kruskal k = new Kruskal(rows*cols);
                k.initializeGraph(BinrayRobotRegions.get(r),true);
                k.performKruskal();
                MSTs.add(k.getAllNewEdges());
            }
            return MSTs;
        }



        private boolean isNumeric(String s) {
            return s.matches("[-+]?\\d*\\.?\\d+");
        }

        private  boolean isNumber(String string) {
            try {
                Double.parseDouble(string);
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }


    private Color generateRandomColor(Color mix) {
        Random random = new Random();
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        // mix the color
        if (mix != null) {
            double alpha=0.2;
            red = (int)((1-alpha)*red + alpha*mix.getRed());
            green = (int)((1-alpha)*green + alpha*mix.getGreen()) ;
            blue = (int)((1-alpha)*blue + alpha*mix.getBlue()) ;
        }

        return new Color(red, green, blue);
    }



    private class FinalPaths extends JPanel {

        int Nrob;
        int[][] Assign;
        boolean[][] robotBin;
        Color primaryColor = Color.red;
        Color secondaryColor = Color.blue;
        GradientPaint gpVertical;
        ArrayList<ArrayList<Integer[]>> AllRealPaths;
        boolean dispMST;
        ArrayList<Vector> KruskalMSTS;
        int SizeToPaintBorder;

        FinalPaths(ArrayList<Vector> MSTS,int[][] Assign, int Nrob, boolean[][] robotBin,
                   ArrayList<ArrayList<Integer[]>> AllRealPaths, boolean disp) {
            this.Nrob = Nrob;
            this.Assign = Assign;
            this.robotBin = robotBin;
            this.AllRealPaths =AllRealPaths;
            this.dispMST = disp;
            this.KruskalMSTS = MSTS;
            this.SizeToPaintBorder = 4;
            this.gpVertical = new GradientPaint(5, 5, primaryColor, 10, 5, secondaryColor, true);
        }

        void setDispMST(boolean v){dispMST=v;}

        private void paint(){

            removeAll();
            int RealRows=2*rows;
            int RealCols=2*cols;


            int[][][] TypesOfLines = new int[RealRows][RealCols][2];
            int fromI,fromJ,toI,toJ;
            int [][] BorderToPaint = new int[RealRows][RealCols];

            int indxadd1,indxadd2;
            for (int r=0;r<Nrob;r++){
                for (Integer[] connection : AllRealPaths.get(r)){
                    if (TypesOfLines[connection[0]][connection[1]][0]==0) {
                        indxadd1 = 0;
                    }else{
                        indxadd1 = 1;
                    }
                    if (TypesOfLines[connection[2]][connection[3]][0]==0) {
                        indxadd2 = 0;
                    }else{
                        indxadd2 = 1;
                    }

                    if (connection[0].equals(connection[2])){ //Horizontal connection (Line types: 2,3)
                        if (connection[1]>connection[3]){
                            TypesOfLines[connection[0]][connection[1]][indxadd1]=2;
                            TypesOfLines[connection[2]][connection[3]][indxadd2]=3;
                        }else{
                            TypesOfLines[connection[0]][connection[1]][indxadd1]=3;
                            TypesOfLines[connection[2]][connection[3]][indxadd2]=2;
                        }
                    }else{ //Vertical connection (Line types: 1,4)
                        if (connection[0]>connection[2]){
                            TypesOfLines[connection[0]][connection[1]][indxadd1] =1;
                            TypesOfLines[connection[2]][connection[3]][indxadd2]=4;
                        }else{
                            TypesOfLines[connection[0]][connection[1]][indxadd1] =4;
                            TypesOfLines[connection[2]][connection[3]][indxadd2]=1;
                        }
                    }
                }


                if (dispMST){
                    for (int e = 0; e < KruskalMSTS.get(r).size(); e++) {
                        Edge curEdge = (Edge) KruskalMSTS.get(r).get(e);
                        fromI = curEdge.from / (cols);
                        fromJ = curEdge.from % cols;
                        toI = curEdge.to / cols;
                        toJ = curEdge.to % cols;

                        if (fromI==toI){ //Horizontal movement --> down
                            if (fromJ>toJ) {
                                BorderToPaint[2*fromI][2*fromJ]+=1;
                                BorderToPaint[2*fromI][2*toJ+1]+=1;
                            }else{
                                BorderToPaint[2*fromI][2*fromJ+1]+=1;
                                BorderToPaint[2*fromI][2*toJ]+=1;
                            }
                        }else if (fromJ==toJ){ //Vertical movement --> right
                            if (fromI>toI) {
                                BorderToPaint[2*fromI][2*fromJ]+=2;
                                BorderToPaint[2*toI+1][2*fromJ]+=2;
                            }else{
                                BorderToPaint[2*fromI+1][2*fromJ]+=2;
                                BorderToPaint[2*toI][2*fromJ]+=2;
                            }
                        }

                    }
                }

            }

            setLayout(new GridLayout(RealRows+1, RealCols+1));
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            for (int i = 0; i < RealRows+1; i++) {
                for (int j = -1; j < RealCols; j++){

                    if (i < RealRows && j >= 0) {
                        if (robotBin[i/2][j/2] && i%2==0 && j%2==0) {
                            RobotCell pan = new RobotCell();//(Color.WHITE,ColorsNr[Assign[i][j]]);
                            //pan.setPreferredSize(new Dimension(getWidth(), getHeight()));
                            pan.setBackground(Color.WHITE);
                            if (dispMST) {paintBorders(pan, BorderToPaint[i][j], ColorsNr[Assign[i/2][j/2]]);}
                            else {pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));}

                            pan.revalidate();
                            pan.repaint();
                            add(pan);
                        }else{
                            JPanel pan = new JPanel();
                            pan.setEnabled(true);
                            pan.setBackground(Color.WHITE);
                            if (Assign[i/2][j/2] == Nrob) {
                                pan.setBackground(Color.BLACK);
                            } else {
                                pan.setBackground(Color.WHITE);
                                if (dispMST) {paintBorders(pan, BorderToPaint[i][j], ColorsNr[Assign[i/2][j/2]]);}
                                else {pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));}

                                //pan.add(new DrawADashedLine(ColorsNr[Assign[i/2][j/2]],TypesOfLines[i][j]));
                                paintBordersForPaths(pan,TypesOfLines[i][j], ColorsNr[Assign[i/2][j/2]]);

                            }
                            //pan.setPreferredSize(new Dimension(getWidth(), getHeight()));
                            pan.revalidate();
                            pan.repaint();
                            add(pan);
                        }
                    }
                    else if (i ==RealRows && j < 0) {
                        JPanel pan = new JPanel();
                        pan.revalidate();
                        pan.repaint();
                        add(pan);
                    }
                    else if (i ==RealRows){
                        JPanel pan = new JPanel();
                        pan.add(new ScalingLabel(Integer.toString(j)));
                        pan.revalidate();
                        pan.repaint();
                        add(pan);
                    }
                    else if (j < 0){
                        JPanel pan = new JPanel();
                        pan.add(new ScalingLabel(Integer.toString(i)));
                        pan.revalidate();
                        pan.repaint();
                        add(pan);
                    }
                }
            }
        }


        private void paintBordersForPaths(JPanel pan,int[] LinesToAdd, Color c){
            ArrayList<JPanel> subJPanelsList = new ArrayList<>();

            pan.setLayout(new GridLayout(2, 2));

            for (int i=0;i<4;i++){
                subJPanelsList.add(new JPanel());
                subJPanelsList.get(i).setBackground(Color.WHITE);
                //subJPanelsList.get(i).setBorder(BorderFactory.createDashedBorder(c));
                subJPanelsList.get(i).setBorder(BorderFactory.createLineBorder(Color.WHITE));
            }



            switch (LinesToAdd[0]) {
                case 1: subJPanelsList.get(0).setBorder(BorderFactory.createMatteBorder(0, 0, 0, SizeToPaintBorder, c)); break; //up
                case 2:  subJPanelsList.get(0).setBorder(BorderFactory.createMatteBorder(0, 0, SizeToPaintBorder, 0, c));break; //left
                case 3:  subJPanelsList.get(3).setBorder(BorderFactory.createMatteBorder(SizeToPaintBorder, 0, 0, 0, c)); break; //right
                case 4:  subJPanelsList.get(3).setBorder(BorderFactory.createMatteBorder(0, SizeToPaintBorder, 0, 0, c));break; //down
            }

            switch (LinesToAdd[1]) {
                case 1: subJPanelsList.get(1).setBorder(BorderFactory.createMatteBorder(0, SizeToPaintBorder, 0, 0, c)); break; //up
                case 2:  subJPanelsList.get(2).setBorder(BorderFactory.createMatteBorder(SizeToPaintBorder, 0, 0, 0, c));break; //left
                case 3:  subJPanelsList.get(1).setBorder(BorderFactory.createMatteBorder(0, 0, SizeToPaintBorder, 0, c)); break; //right
                case 4:  subJPanelsList.get(2).setBorder(BorderFactory.createMatteBorder(0, 0, 0, SizeToPaintBorder, c));break; //down
            }





            for (int i=0;i<4;i++){pan.add(subJPanelsList.get(i));}

        }

        private void paintBorders(JPanel pan, int borderV, Color c){
            if (borderV==2) {
                pan.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
                        BorderFactory.createMatteBorder(0, 0, 0, SizeToPaintBorder, c)));
            }
            else if(borderV==1){
                pan.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
                        BorderFactory.createMatteBorder(0, 0, SizeToPaintBorder, 0, c)));
            }
            else if (borderV==3){
                pan.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.BLACK),
                        BorderFactory.createMatteBorder(0, 0, SizeToPaintBorder, SizeToPaintBorder, c)));
            }
            else{
                pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }


    }



    private class DARPPane extends JPanel {

        int Nrob;
        int[][] Assign;
        boolean[][] robotBin;
        Color primaryColor = Color.red;
        Color secondaryColor = Color.blue;
        GradientPaint gpVertical;



        DARPPane(int[][] Assign, int Nrob, boolean[][] robotBin) {
            this.Nrob = Nrob;
            this.Assign = Assign;
            this.robotBin = robotBin;
            this.gpVertical = new GradientPaint(5, 5, primaryColor, 10, 5, secondaryColor, true);
        }

        private void paint(){

            removeAll();
            setLayout(new GridLayout(rows+1, cols+1));
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            for (int i = 0; i < rows+1; i++) {
                for (int j = -1; j < cols; j++){

                    if (i < rows && j >= 0) {
                        if (robotBin[i][j]) {
                            RobotCell pan = new RobotCell();//(Color.WHITE,ColorsNr[Assign[i][j]]);
                            pan.setPreferredSize(new Dimension(getWidth(), getHeight()));
                            pan.setBackground(ColorsNr[Assign[i][j]]);
                            pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            pan.revalidate();
                            pan.repaint();
                            add(pan);
                        } else {
                            JPanel pan = new JPanel();
                            pan.setEnabled(true);
                            if (Assign[i][j] == Nrob) {
                                pan.setBackground(Color.BLACK);
                            } else {
                                pan.setBackground(ColorsNr[Assign[i][j]]);
                            }
                            pan.setPreferredSize(new Dimension(getWidth(), getHeight()));
                            pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                            pan.revalidate();
                            pan.repaint();
                            add(pan);
                        }
                    }
                    else if (i ==rows && j < 0) {
                        JPanel pan = new JPanel();
                        pan.revalidate();
                        pan.repaint();
                        add(pan);
                    }
                    else if (i ==rows){
                        JPanel pan = new JPanel();
                        pan.add(new ScalingLabel(Integer.toString(j)));
                        pan.revalidate();
                        pan.repaint();
                        add(pan);
                    }
                    else if (j < 0){
                        JPanel pan = new JPanel();
                        pan.add(new ScalingLabel(Integer.toString(i)));
                        pan.revalidate();
                        pan.repaint();
                        add(pan);
                    }
                }
            }
        }

    }



    private class GridPane extends JPanel {

        boolean enable=true;

        GridPane() {

            setLayout(new GridLayout(rows+1, cols+1));
            setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));

            for (int i = 0; i < rows+1; i++) {
                for (int j = -1; j < cols; j++) {
                    JPanel pan = new JPanel();

                    pan.setEnabled(true);
                    pan.setBackground(Color.WHITE);
                    pan.setPreferredSize(new Dimension(getWidth(), getHeight()));
                    if (i < rows && j >= 0) {
                        pan.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        pan.addMouseListener(new BoxListener()); // add a mouse listener to make the panels clickable
                        pan.setName(i + " " + j);
                    }
                    else if (i ==rows && j < 0) {pan.add(new JLabel(" "));}
                    else if (i ==rows){pan.add(new ScalingLabel(Integer.toString(j)));}
                    else if (j < 0){pan.add(new ScalingLabel(Integer.toString(i)));}

                    add(pan);
                }
            }
        }
    }


    private class BoxListener extends MouseAdapter
    {

        public void mousePressed(MouseEvent me) {
            if (CurrentIDXAdd>=0 && ColorGrid.enable) {
                JPanel clickedBox = (JPanel) me.getSource(); // get the reference to the box that was clicked
                clickedBox.setBackground(CurrColor);
                String[] iAndJ = clickedBox.getName().split("\\s+");
                EnvironmentGrid[Integer.parseInt(iAndJ[0])][Integer.parseInt(iAndJ[1])] = CurrentIDXAdd;
            }
        }

        public void mouseEntered(MouseEvent me){
            if (me.getModifiers() == MouseEvent.BUTTON1_MASK && CurrentIDXAdd!=2) {
                if (CurrentIDXAdd >= 0 && ColorGrid.enable) {
                    JPanel clickedBox = (JPanel) me.getSource(); // get the reference to the box that was clicked
                    clickedBox.setBackground(CurrColor);
                    String[] iAndJ = clickedBox.getName().split("\\s+");
                    EnvironmentGrid[Integer.parseInt(iAndJ[0])][Integer.parseInt(iAndJ[1])] = CurrentIDXAdd;
                }
            }
        }
    }


    private class ScalingLabel extends JLabel implements ComponentListener {

        ScalingLabel(String text) {
            super(text);
            addComponentListener(this);
        }

        @Override
        public void componentHidden(ComponentEvent e) {}

        @Override
        public void componentMoved(ComponentEvent e) {}

        @Override
        public void componentResized(ComponentEvent e) {
            Font font = getFont();
            FontMetrics metrics = getFontMetrics(font);
            float size = font.getSize2D();
            float textWidth = metrics.stringWidth(getText());
            size = (float) Math.floor((getWidth() / textWidth) * size);
            setFont(font.deriveFont(size));
        }

        @Override
        public void componentShown(ComponentEvent e) {}
    }


    private class DrawADashedLine extends JPanel{
        Color robotColor;
        int[] dir;

        DrawADashedLine(Color c, int[] direction) {
            this.robotColor = c;
            this.dir = direction;
        }

        public void paintComponent(Graphics g){
            super.paintComponent(g);
            //Graphics2D g2 = (Graphics2D) g;
            //g.setColor(robotColor);
            //Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
            //g2.setStroke(dashed);
            g.setColor(robotColor);
            switch (dir[0]) {
                case 1: g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight()/2); break; //up
                case 2: g.drawLine(0, getHeight()/2, getWidth()/2, getHeight()/2); break; //left
                case 3: g.drawLine(getWidth(), getHeight()/2, getWidth()/2, getHeight()/2); break; //right
                case 4: g.drawLine(getWidth()/2, getHeight(), getWidth()/2, getHeight()/2); break; //down
            }
            switch (dir[1]) {
                case 1: g.drawLine(getWidth()/2, 0, getWidth()/2, getHeight()/2); break;
                case 2: g.drawLine(0, getHeight()/2, getWidth()/2, getHeight()/2); break;
                case 3: g.drawLine(getWidth(), getHeight()/2, getWidth()/2, getHeight()/2); break;
                case 4: g.drawLine(getWidth()/2, getHeight(), getWidth()/2, getHeight()/2); break;
            }

            g.dispose();
            repaint();
        }
    }


    private class MyDrawPanel extends JPanel{

        MyDrawPanel() {
            // set a preferred size for the custom panel.
            setPreferredSize(new Dimension(50,50));
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }


        public void paintComponent(Graphics g){
            g.setColor(CurrColor);
            g.fillRect(0,0,50,50);
        }
    }


    private class RobotCell extends  JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // Retains the previous state
            Paint oldPaint = g2.getPaint();

            // Fills the circle with solid blue color
            g2.setColor(new Color(0x0153CC));
            g2.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);


            // Adds shadows at the top
            Paint p;
            p = new GradientPaint(0, 0, new Color(0.0f, 0.0f, 0.0f, 0.4f),
                    0, getHeight(), new Color(0.0f, 0.0f, 0.0f, 0.0f));
            g2.setPaint(p);
            g2.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);

            // Adds highlights at the bottom
            p = new GradientPaint(0, 0, new Color(1.0f, 1.0f, 1.0f, 0.0f),
                    0, getHeight(), new Color(1.0f, 1.0f, 1.0f, 0.4f));
            g2.setPaint(p);
            g2.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);

            // Creates dark edges for 3D effect
            p = new RadialGradientPaint(new Point2D.Double(getWidth() / 2.0,
                    getHeight() / 2.0), getWidth() / 2.0f,
                    new float[] { 0.0f, 1.0f },
                    new Color[] { new Color(6, 76, 160, 127),
                            new Color(0.0f, 0.0f, 0.0f, 0.8f) });
            g2.setPaint(p);
            g2.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);

            // Adds oval inner highlight at the bottom
            p = new RadialGradientPaint(new Point2D.Double(getWidth() / 2.0,
                    getHeight() * 1.5), getWidth() / 2.3f,
                    new Point2D.Double(getWidth() / 2.0, getHeight() * 1.75 + 6),
                    new float[] { 0.0f, 0.8f },
                    new Color[] { new Color(64, 142, 203, 255),
                            new Color(64, 142, 203, 0) },
                    RadialGradientPaint.CycleMethod.NO_CYCLE,
                    RadialGradientPaint.ColorSpaceType.SRGB,
                    AffineTransform.getScaleInstance(1.0, 0.5));
            g2.setPaint(p);
            g2.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);

            // Adds oval specular highlight at the top left
            p = new RadialGradientPaint(new Point2D.Double(getWidth() / 2.0,
                    getHeight() / 2.0), getWidth() / 1.4f,
                    new Point2D.Double(45.0, 25.0),
                    new float[] { 0.0f, 0.5f },
                    new Color[] { new Color(1.0f, 1.0f, 1.0f, 0.4f),
                            new Color(1.0f, 1.0f, 1.0f, 0.0f) },
                    RadialGradientPaint.CycleMethod.NO_CYCLE);
            g2.setPaint(p);
            g2.fillOval(getWidth() / 4, getHeight() / 4, getWidth() / 2, getHeight() / 2);


            // Restores the previous state
            g2.setPaint(oldPaint);

        }


    }



}
