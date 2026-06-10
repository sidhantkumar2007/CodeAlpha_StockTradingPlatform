import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

/**
 * CodeAlpha — Task 2: Stock Trading Platform (GUI Version)
 * Built with Java Swing
 */
public class StockTradingPlatformGUI extends JFrame {

    // ── Colors & Fonts ────────────────────────────────────────────────────
    static final Color BG      = new Color(10, 14, 26);
    static final Color CARD    = new Color(20, 27, 45);
    static final Color CARD2   = new Color(26, 35, 56);
    static final Color GREEN   = new Color(16, 185, 129);
    static final Color RED     = new Color(239, 68, 68);
    static final Color BLUE    = new Color(99, 102, 241);
    static final Color YELLOW  = new Color(245, 158, 11);
    static final Color TEXT    = new Color(248, 250, 252);
    static final Color SUBTEXT = new Color(148, 163, 184);
    static final Color BORDER  = new Color(40, 55, 80);
    static final Font  TITLE_F = new Font("Segoe UI", Font.BOLD, 20);
    static final Font  HDR_F   = new Font("Segoe UI", Font.BOLD, 12);
    static final Font  BODY_F  = new Font("Segoe UI", Font.PLAIN, 12);
    static final Font  MONO_F  = new Font("Consolas", Font.PLAIN, 12);

    // ── Data Model ────────────────────────────────────────────────────────
    static class Stock {
        String symbol, company; double price, prev; double change;
        Stock(String s, String c, double p) { symbol=s; company=c; price=p; prev=p; }
        void update(double np) { prev=price; price=np; change=((price-prev)/prev)*100; }
        String trend() { return change>0 ? "▲ +"+String.format("%.2f",change)+"%" : change<0 ? "▼ "+String.format("%.2f",change)+"%" : "─ 0.00%"; }
    }
    static class Transaction {
        String type, symbol, time; int qty; double price;
        Transaction(String t, String s, int q, double p) {
            type=t; symbol=s; qty=q; price=p;
            time=LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd HH:mm"));
        }
    }

    Map<String, Stock> market = new LinkedHashMap<>();
    Map<String, Integer> holdings = new LinkedHashMap<>();
    Map<String, Double>  avgCost  = new HashMap<>();
    List<Transaction> txHistory   = new ArrayList<>();
    double balance = 100_000.0;
    final double INIT_BAL = 100_000.0;
    Random rng = new Random();

    // ── UI Components ─────────────────────────────────────────────────────
    DefaultTableModel marketModel, portfolioModel, txModel;
    JLabel balLabel, totalLabel, plLabel, plPctLabel;

    public StockTradingPlatformGUI() {
        initMarket();
        setTitle("Stock Trading Platform — CodeAlpha");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(0,0));
        root.setBackground(BG);
        root.add(buildHeader(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10,10));
        center.setBackground(BG);
        center.setBorder(new EmptyBorder(12,12,12,12));
        center.add(buildTopStats(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, buildMarketPanel(), buildRightPanel());
        split.setBackground(BG); split.setBorder(null); split.setDividerSize(6);
        split.setDividerLocation(580);
        center.add(split, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        setContentPane(root);
        startMarketTimer();
        refreshAll();
        setVisible(true);
    }

    void initMarket() {
        market.put("AAPL",  new Stock("AAPL",  "Apple Inc.",        189.50));
        market.put("GOOGL", new Stock("GOOGL", "Alphabet Inc.",     141.80));
        market.put("MSFT",  new Stock("MSFT",  "Microsoft Corp.",   420.25));
        market.put("AMZN",  new Stock("AMZN",  "Amazon.com",        182.60));
        market.put("TSLA",  new Stock("TSLA",  "Tesla Inc.",        248.90));
        market.put("META",  new Stock("META",  "Meta Platforms",    512.30));
        market.put("NVDA",  new Stock("NVDA",  "NVIDIA Corp.",      875.40));
        market.put("NFLX",  new Stock("NFLX",  "Netflix Inc.",      628.70));
    }

    // ── Header ────────────────────────────────────────────────────────────
    JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(CARD); p.setPreferredSize(new Dimension(0,60));
        p.setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        JLabel t = new JLabel("  📈 Stock Trading Platform"); t.setFont(TITLE_F); t.setForeground(TEXT);
        JLabel s = new JLabel("Paper Trading Simulator — CodeAlpha  "); s.setFont(BODY_F); s.setForeground(SUBTEXT);
        p.add(t, BorderLayout.WEST); p.add(s, BorderLayout.EAST); return p;
    }

    // ── Top Stats Bar ─────────────────────────────────────────────────────
    JPanel buildTopStats() {
        JPanel p = new JPanel(new GridLayout(1,4,10,0));
        p.setBackground(BG); p.setPreferredSize(new Dimension(0,75));
        balLabel  = statTile("💰 Cash Balance",  "$100,000.00", TEXT);
        totalLabel= statTile("📊 Total Value",   "$100,000.00", TEXT);
        plLabel   = statTile("📈 Profit / Loss", "$0.00",       TEXT);
        plPctLabel= statTile("📉 Return",        "0.00%",       TEXT);
        p.add(balLabel); p.add(totalLabel); p.add(plLabel); p.add(plPctLabel);
        return p;
    }

    JLabel statTile(String lbl, String val, Color vc) {
        JLabel l = new JLabel("<html><div style='text-align:center'><div style='font-size:8px;color:#94a3b8'>" + lbl + "</div><div style='font-size:16px;font-weight:bold;color:" + toHex(vc) + "'>" + val + "</div></div></html>", JLabel.CENTER);
        l.setOpaque(true); l.setBackground(CARD); l.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(6,6,6,6))); return l;
    }

    // ── Market Panel ──────────────────────────────────────────────────────
    JPanel buildMarketPanel() {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(BG);

        // Market table
        String[] cols = {"Symbol","Company","Price","Change","Trend"};
        marketModel = new DefaultTableModel(cols,0){ public boolean isCellEditable(int r,int c){return false;} };
        JTable mkt = styledTable(marketModel);
        mkt.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                String s=v!=null?v.toString():"";
                l.setForeground(s.startsWith("▲")?GREEN:s.startsWith("▼")?RED:SUBTEXT);
                l.setFont(HDR_F); l.setHorizontalAlignment(JLabel.CENTER); return l;
            }
        });

        // Buy/Sell panel
        JPanel trade = new JPanel(); trade.setLayout(new BoxLayout(trade, BoxLayout.Y_AXIS));
        trade.setBackground(CARD); trade.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER), new EmptyBorder(12,12,12,12)));

        JLabel tl = new JLabel("⚡ Quick Trade"); tl.setFont(HDR_F); tl.setForeground(TEXT); tl.setAlignmentX(0);
        String[] symbols = market.keySet().toArray(new String[0]);
        JComboBox<String> symBox = styledCombo(symbols);
        JTextField qtyField = styledField("Quantity...");
        JButton buyBtn  = tradeBtn("BUY",  GREEN);
        JButton sellBtn = tradeBtn("SELL", RED);
        JButton simBtn  = tradeBtn("🔄 Simulate Market", BLUE);

        buyBtn.addActionListener(e -> executeTrade("BUY",  (String)symBox.getSelectedItem(), qtyField.getText()));
        sellBtn.addActionListener(e -> executeTrade("SELL", (String)symBox.getSelectedItem(), qtyField.getText()));
        simBtn.addActionListener(e -> { simulateMarket(); refreshAll(); });

        JPanel btnRow = new JPanel(new GridLayout(1,2,8,0)); btnRow.setBackground(CARD); btnRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        btnRow.add(buyBtn); btnRow.add(sellBtn);

        trade.add(tl); trade.add(Box.createVerticalStrut(10));
        trade.add(subLabel("Stock Symbol")); trade.add(Box.createVerticalStrut(3)); trade.add(symBox); trade.add(Box.createVerticalStrut(8));
        trade.add(subLabel("Quantity")); trade.add(Box.createVerticalStrut(3)); trade.add(qtyField); trade.add(Box.createVerticalStrut(8));
        trade.add(btnRow); trade.add(Box.createVerticalStrut(6)); trade.add(simBtn);

        p.add(sectionTitle("📊 Live Market"), BorderLayout.NORTH);
        p.add(new JScrollPane(mkt){{setBackground(CARD);getViewport().setBackground(CARD);setBorder(BorderFactory.createLineBorder(BORDER));}}, BorderLayout.CENTER);
        p.add(trade, BorderLayout.SOUTH);
        return p;
    }

    // ── Right Panel: Portfolio + History ──────────────────────────────────
    JPanel buildRightPanel() {
        JPanel p = new JPanel(new BorderLayout(0,8));
        p.setBackground(BG);

        // Portfolio table
        String[] pc = {"Symbol","Qty","Avg Cost","Curr Price","P/L"};
        portfolioModel = new DefaultTableModel(pc,0){ public boolean isCellEditable(int r,int c){return false;} };
        JTable pt = styledTable(portfolioModel);
        pt.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                String s=v!=null?v.toString():"";
                l.setForeground(s.startsWith("+")?GREEN:s.startsWith("-")?RED:SUBTEXT);
                l.setFont(HDR_F); return l;
            }
        });

        // Transaction history
        String[] tc = {"Time","Type","Symbol","Qty","Price","Total"};
        txModel = new DefaultTableModel(tc,0){ public boolean isCellEditable(int r,int c){return false;} };
        JTable tt = styledTable(txModel);
        tt.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer(){
            public Component getTableCellRendererComponent(JTable t,Object v,boolean sel,boolean foc,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,sel,foc,r,c);
                l.setForeground("BUY".equals(v)?GREEN:RED); l.setFont(HDR_F); l.setHorizontalAlignment(JLabel.CENTER); return l;
            }
        });

        JSplitPane sp = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        sp.setBackground(BG); sp.setBorder(null); sp.setDividerSize(6); sp.setDividerLocation(280);

        JPanel top = new JPanel(new BorderLayout(0,6)); top.setBackground(BG);
        top.add(sectionTitle("💼 My Portfolio"), BorderLayout.NORTH);
        top.add(new JScrollPane(pt){{setBackground(CARD);getViewport().setBackground(CARD);setBorder(BorderFactory.createLineBorder(BORDER));}}, BorderLayout.CENTER);

        JPanel bot = new JPanel(new BorderLayout(0,6)); bot.setBackground(BG);
        bot.add(sectionTitle("📋 Transaction History"), BorderLayout.NORTH);
        bot.add(new JScrollPane(tt){{setBackground(CARD);getViewport().setBackground(CARD);setBorder(BorderFactory.createLineBorder(BORDER));}}, BorderLayout.CENTER);

        sp.setTopComponent(top); sp.setBottomComponent(bot);
        p.add(sp);
        return p;
    }

    // ── Trade Logic ───────────────────────────────────────────────────────
    void executeTrade(String type, String symbol, String qtyStr) {
        try {
            int qty = Integer.parseInt(qtyStr.trim());
            if (qty <= 0) { showError("Quantity must be positive."); return; }
            Stock s = market.get(symbol);
            if (s == null) { showError("Symbol not found."); return; }

            if ("BUY".equals(type)) {
                double cost = s.price * qty;
                if (cost > balance) { showError(String.format("Insufficient balance.\nNeed $%.2f, have $%.2f", cost, balance)); return; }
                balance -= cost;
                int cur = holdings.getOrDefault(symbol, 0);
                double curAvg = avgCost.getOrDefault(symbol, 0.0);
                avgCost.put(symbol, (curAvg*cur + s.price*qty)/(cur+qty));
                holdings.put(symbol, cur+qty);
            } else {
                int owned = holdings.getOrDefault(symbol, 0);
                if (owned < qty) { showError("You only own "+owned+" shares of "+symbol); return; }
                balance += s.price * qty;
                if (owned-qty == 0) { holdings.remove(symbol); avgCost.remove(symbol); }
                else holdings.put(symbol, owned-qty);
            }
            txHistory.add(0, new Transaction(type, symbol, qty, s.price));
            refreshAll();
            JOptionPane.showMessageDialog(this,
                String.format("✅ %s %d shares of %s @ $%.2f", type, qty, symbol, s.price),
                type+" Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) { showError("Enter a valid quantity."); }
    }

    void simulateMarket() {
        for (Stock s : market.values()) {
            double chg = (rng.nextDouble()*10)-5;
            s.update(Math.max(1.0, Math.round((s.price*(1+chg/100))*100.0)/100.0));
        }
    }

    // ── Refresh UI ────────────────────────────────────────────────────────
    void refreshAll() {
        // Market table
        marketModel.setRowCount(0);
        for (Stock s : market.values()) {
            marketModel.addRow(new Object[]{s.symbol, s.company,
                String.format("$%.2f", s.price), String.format("%.2f%%", s.change), s.trend()});
        }
        // Portfolio table
        portfolioModel.setRowCount(0);
        double stockVal = 0;
        for (Map.Entry<String,Integer> e : holdings.entrySet()) {
            Stock s = market.get(e.getKey()); if(s==null) continue;
            int qty=e.getValue(); double avg=avgCost.getOrDefault(e.getKey(),0.0);
            double pl=(s.price-avg)*qty; stockVal+=s.price*qty;
            portfolioModel.addRow(new Object[]{e.getKey(), qty,
                String.format("$%.2f",avg), String.format("$%.2f",s.price),
                (pl>=0?"+":"")+String.format("$%.2f",pl)});
        }
        // Transaction table
        txModel.setRowCount(0);
        for (Transaction t : txHistory)
            txModel.addRow(new Object[]{t.time,t.type,t.symbol,t.qty,
                String.format("$%.2f",t.price), String.format("$%.2f",t.qty*t.price)});

        // Stats
        double total = balance + stockVal;
        double pl    = total - INIT_BAL;
        double pct   = (pl/INIT_BAL)*100;
        Color plColor = pl >= 0 ? GREEN : RED;
        balLabel.setText(html("💰 Cash Balance","$"+String.format("%.2f",balance),TEXT));
        totalLabel.setText(html("📊 Total Value","$"+String.format("%.2f",total),TEXT));
        plLabel.setText(html("📈 Profit / Loss",(pl>=0?"+":"")+String.format("$%.2f",pl),plColor));
        plPctLabel.setText(html("📉 Return",(pct>=0?"+":"")+String.format("%.2f%%",pct),plColor));
    }

    // ── Market Auto-Update ────────────────────────────────────────────────
    void startMarketTimer() {
        javax.swing.Timer t = new javax.swing.Timer(8000, e -> { simulateMarket(); refreshAll(); });
        t.start();
    }

    // ── Util Builders ─────────────────────────────────────────────────────
    JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setBackground(CARD); t.setForeground(TEXT); t.setFont(BODY_F);
        t.setRowHeight(32); t.setShowGrid(false); t.setIntercellSpacing(new Dimension(0,1));
        t.setSelectionBackground(new Color(99,102,241,80)); t.setSelectionForeground(TEXT);
        t.getTableHeader().setBackground(BG); t.getTableHeader().setForeground(SUBTEXT);
        t.getTableHeader().setFont(HDR_F); t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0,0,1,0,BORDER));
        return t;
    }
    JComboBox<String> styledCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(CARD2); cb.setForeground(TEXT); cb.setFont(BODY_F);
        cb.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); cb.setAlignmentX(0); return cb;
    }
    JTextField styledField(String ph) {
        JTextField f = new JTextField(ph); f.setBackground(CARD2); f.setForeground(SUBTEXT);
        f.setCaretColor(TEXT); f.setFont(BODY_F);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),new EmptyBorder(5,8,5,8)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE,34)); f.setAlignmentX(0);
        f.addFocusListener(new FocusAdapter(){
            public void focusGained(FocusEvent e){if(f.getText().equals(ph)){f.setText("");f.setForeground(TEXT);}}
            public void focusLost(FocusEvent e) {if(f.getText().isEmpty()){f.setText(ph);f.setForeground(SUBTEXT);}}
        });
        return f;
    }
    JButton tradeBtn(String text, Color color) {
        JButton b = new JButton(text); b.setFont(HDR_F); b.setBackground(color); b.setForeground(Color.WHITE);
        b.setBorder(new EmptyBorder(8,12,8,12)); b.setFocusPainted(false); b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(0); b.setMaximumSize(new Dimension(Integer.MAX_VALUE,36));
        b.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){b.setBackground(color.darker());}
            public void mouseExited(MouseEvent e) {b.setBackground(color);}
        });
        return b;
    }
    JLabel sectionTitle(String text) {
        JLabel l = new JLabel(text); l.setFont(HDR_F); l.setForeground(TEXT);
        l.setBorder(new EmptyBorder(0,0,4,0)); return l;
    }
    JLabel subLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(new Font("Segoe UI",Font.PLAIN,10)); l.setForeground(SUBTEXT); l.setAlignmentX(0); return l;
    }
    String html(String lbl, String val, Color c) {
        return "<html><div style='text-align:center'><div style='font-size:8px;color:#94a3b8'>"+lbl+"</div><div style='font-size:15px;font-weight:bold;color:"+toHex(c)+"'>"+val+"</div></div></html>";
    }
    String toHex(Color c) { return String.format("#%02x%02x%02x",c.getRed(),c.getGreen(),c.getBlue()); }
    void showError(String msg) { JOptionPane.showMessageDialog(this,msg,"Error",JOptionPane.ERROR_MESSAGE); }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored){}
        SwingUtilities.invokeLater(StockTradingPlatformGUI::new);
    }
}
