import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.Random;

public class TubaroesAquaticos extends JPanel implements MouseMotionListener, Runnable {
    private double[][] tubaroes;
    private double[][] velocidades;
    private double xMouse, yMouse;
    private final int NUM_TUBAROES = 4;
    private double[][] algasOffsets;
    private double[][] pedras;
    private int frameCount = 0;
    private double[] tubaraoVelocidadeBase;
    private Color[] tubaraoCores;

    public TubaroesAquaticos() {
        tubaroes = new double[NUM_TUBAROES][2];
        velocidades = new double[NUM_TUBAROES][2];
        tubaraoVelocidadeBase = new double[NUM_TUBAROES];
        algasOffsets = new double[60][20];
        pedras = new double[50][2];

        tubaraoCores = new Color[]{new Color(100, 100, 200), new Color(150, 100, 250), new Color(100, 150, 200), new Color(50, 100, 150)};

        Random random = new Random();
        for (int i = 0; i < NUM_TUBAROES; i++) {
            tubaroes[i][0] = random.nextDouble() * 700 + 50;
            tubaroes[i][1] = random.nextDouble() * 500 + 50;
            velocidades[i][0] = random.nextDouble() * 0.5 - 0.25;
            velocidades[i][1] = random.nextDouble() * 0.5 - 0.25;
            tubaraoVelocidadeBase[i] = 0.1 + i * 0.01;
        }

        for (int i = 0; i < algasOffsets.length; i++) {
            for (int j = 0; j < algasOffsets[i].length; j++) {
                algasOffsets[i][j] = random.nextDouble() * 0.1;
            }
        }

        for (int i = 0; i < pedras.length; i++) {
            pedras[i][0] = random.nextDouble() * 800;
            pedras[i][1] = 600 + random.nextDouble() * 50;
        }

        addMouseMotionListener(this);
        Thread animacao = new Thread(this);
        animacao.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        desenharFundo(g);

        for (int i = 0; i < NUM_TUBAROES; i++) {
            desenharTubarao(g, tubaroes[i][0], tubaroes[i][1], i);
        }

        moverTubaroes();
        moverPedras();
        frameCount++;
    }

    private void desenharTubarao(Graphics g, double x, double y, int index) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        GradientPaint gradient = new GradientPaint((int) x - 50, (int) y, tubaraoCores[index].brighter(), (int) x + 50, (int) y, tubaraoCores[index].darker());
        g2d.setPaint(gradient);
        g2d.fillRoundRect((int) x - 50, (int) y - 15, 100, 30, 40, 40);

        int[] xBarbatana = {(int) x - 10, (int) x + 10, (int) x};
        int[] yBarbatana = {(int) y - 20, (int) y - 35, (int) y - 15};
        g2d.setColor(tubaraoCores[index].darker());
        g2d.fillPolygon(xBarbatana, yBarbatana, 3);

        int finOffset = (int) (Math.sin(frameCount * 0.2 + index) * 5);
        g2d.setColor(tubaraoCores[index].darker());
        g2d.fillPolygon(new int[]{(int) x + 10, (int) x + 40, (int) x + 10}, new int[]{(int) y - 8 + finOffset, (int) y - 30 + finOffset, (int) y + 8 + finOffset}, 3);
        g2d.fillPolygon(new int[]{(int) x + 10, (int) x + 40, (int) x + 10}, new int[]{(int) y + 8 - finOffset, (int) y + 30 - finOffset, (int) y - 8 - finOffset}, 3);

        int caudaOffset = (int) (Math.sin(frameCount * 0.3 + index) * 5);
        int[] xCauda = {(int) x + 50, (int) x + 70, (int) x + 50};
        int[] yCauda = {(int) y - 15 + caudaOffset, (int) y + caudaOffset, (int) y + 15 + caudaOffset};
        g2d.fillPolygon(xCauda, yCauda, 3);

        g2d.setColor(Color.WHITE);
        g2d.fillOval((int) x - 30, (int) y - 5, 10, 10);
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int) x - 28, (int) y - 3, 5, 5);
    }

    private void desenharFundo(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint oceanGradient = new GradientPaint(0, 0, new Color(0, 105, 148), 0, getHeight(), new Color(0, 51, 102));
        g2d.setPaint(oceanGradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(173, 216, 230, 80));
        for (int i = 0; i < getWidth(); i += 40) {
            int waveHeight = (int) (15 * Math.sin((i + frameCount * 2) * 0.05) + 12 * Math.sin((i + frameCount * 3) * 0.03));
            g.fillArc(i, waveHeight, 80, 35, 0, 180);
        }

        g.setColor(new Color(34, 139, 34));
        for (int i = 0; i < algasOffsets.length; i++) {
            int baseX = (i * getWidth() / algasOffsets.length);
            int baseY = getHeight() - 50;
            int algaeHeight = (int) (70 + Math.random() * 50);
            int lastX = baseX, lastY = baseY;

            for (int j = 0; j < 5; j++) {
                int offsetY = (int) (Math.sin(frameCount * 0.02 + i) * 5);
                g.drawLine(lastX + j, lastY + offsetY, lastX + j, lastY + offsetY - algaeHeight);
            }
        }

        g.setColor(new Color(222, 184, 135));
        g.fillRect(0, getHeight() - 50, getWidth(), 50);

        // Corrente marítima mais visível
        g.setColor(new Color(70, 130, 180, 150));
        for (int i = 0; i < getWidth(); i += 20) {
            int waveHeight = (int) (5 * Math.sin((i + frameCount * 0.4) * 0.1));
            g.drawLine(i, getHeight() - 30 + waveHeight, i + 20, getHeight() - 30 + waveHeight);
        }

        g.setColor(Color.GRAY);
        for (double[] pedra : pedras) {
            g.fillOval((int) pedra[0], (int) pedra[1], 5, 5);
        }
    }

    private void moverTubaroes() {
        for (int i = 0; i < NUM_TUBAROES; i++) {
            double distanciaX = xMouse - tubaroes[i][0];
            double distanciaY = yMouse - tubaroes[i][1];
            double distancia = Math.sqrt(distanciaX * distanciaX + distanciaY * distanciaY);

            // Movimento do primeiro tubarão
            if (i == 0) {
                tubaroes[i][0] += velocidades[i][0];
                tubaroes[i][1] += velocidades[i][1];

                if (tubaroes[i][0] < 0 || tubaroes[i][0] > getWidth()) {
                    velocidades[i][0] = -velocidades[i][0];
                }
                if (tubaroes[i][1] < 0 || tubaroes[i][1] > getHeight()) {
                    velocidades[i][1] = -velocidades[i][1];
                }
            } else {
                // Comportamento de separação
                if (distancia < 100) {
                    double angulo = Math.atan2(distanciaY, distanciaX);
                    tubaroes[i][0] -= Math.cos(angulo) * 2;
                    tubaroes[i][1] -= Math.sin(angulo) * 2;
                }

                // Movimentação aleatória
                tubaroes[i][0] += velocidades[i][0] * tubaraoVelocidadeBase[i];
                tubaroes[i][1] += velocidades[i][1] * tubaraoVelocidadeBase[i];

                // Verificação de bordas
                if (tubaroes[i][0] < 0 || tubaroes[i][0] > getWidth()) {
                    velocidades[i][0] = -velocidades[i][0];
                }
                if (tubaroes[i][1] < 0 || tubaroes[i][1] > getHeight()) {
                    velocidades[i][1] = -velocidades[i][1];
                }
            }
        }
    }

    private void moverPedras() {
        for (double[] pedra : pedras) {
            pedra[1] += 0.5; // Movimento para baixo
            if (pedra[1] > getHeight()) {
                pedra[1] = 0; // Reseta a pedra
                pedra[0] = Math.random() * getWidth(); // Nova posição aleatória
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        xMouse = e.getX();
        yMouse = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Tubarões Aquáticos");
        TubaroesAquaticos painel = new TubaroesAquaticos();
        frame.add(painel);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

