package io.github.luismanfredi.atividadejogo;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;

// extends significa herdar. Player herda de Sprite, então da para aproveitar atributos e metodos
// antes era feito: sprite.getX(), agora apenas getX(). Tbm não é preciso dai criar um getX() aqui em Player.
public class Player extends Sprite {

    // velocidade de movimento, velocidade x e velocidade y. Cada uma é acessada por velocidade . (x ou y). Exemplo: velocity.x
    public Vector2 velocity = new Vector2(0f, 0f); // ambas velocidades começam em 0

    private float speed = 4f; // velocidade horizontal
    private float gravity = -20f; // gravidade...
    private float jumpStrength = 8f; // força do pulo, velocidade inicial dele
    private boolean grounded = false; // variavel de estado, confere se o personagem está no chão. Impede pulo infinito por exemplo. true = está no chão, e vice versa

    private TiledMapTileLayer collisionLayer; // variavel com a camada de colissão do Tiled, explico mais no readme

    // construtor de Player (Igual quando você faz Scanner scanner = new Scanner(System.in))
    // Nesse caso ele aceita um sprite de um personagem, e a camada de colissõa do Tiled
    public Player(Sprite sprite, TiledMapTileLayer collisionLayer) {
        super(sprite); // super chama o construtor da classe Pai. Inicializa o sprite do personagem. Basicamente tamo aproveitando a implementação do Sprite
        this.collisionLayer = collisionLayer; // collisionLayer é o que foi recebido pelo construtor. Agora this.collisionLayer é o atributo/variavel dessa clase, que criei ali em cima
        setSize(1f, 1f); // tamnho do personagem, 1 tile exatamente
    }

    // esse metodo atualiza o personagem, ele é chamado a cada frame
    public void update(float delta) {
        // esse delta que ele recebe é basicamente o tempo desde o último frame

        if (!grounded) {
            velocity.y += gravity * delta;
        }

        if (!isCellBlocked(getX() + 0.1f, getY() - 0.01f)  || !isCellBlocked(getX() + getWidth() - 0.01f, getY() - 0.01f)) {
            grounded = false;
        }

        // traduzindo o if:
        // se o personagem não está no chão:
        // gravidade é aplicada no pulo
        // se não entendeu oque é "!grounded" ele só reverte/nega o boolean, se chega false, vira true, e chega true vira false
        // e esse if só vai ser executado se for true, então grounded precisa ser false, melhor pesquisar que essa explicação ficou pessima...

        // posições do personagens salvas, porque?
        // para caso ele colida com algo, a gente volte a posição antiga
        float oldX = getX();
        translateX(velocity.x * delta);

        if (velocity.x > 0) {
            if (isCellBlocked(getX() + getWidth(), getY()) || isCellBlocked(getX() + getWidth(), getY() + getHeight() - 0.01f)) {
                setX(oldX); // posição antiga que ele volta, para isso que serve o setX(posiçãoantiga)
                velocity.x = 0; // velocidade 0 ja que enconstou em parede
            }
        } else if (velocity.x < 0) {
            if (isCellBlocked(getX() , getY()) || isCellBlocked(getX(), getY() + getHeight() - 0.01f)) {
                setX(oldX); // posição antiga que ele volta, para isso que serve o setX(posiçãoantiga)
                velocity.x = 0;
            }
        }


        // Colisão está abaixo, vou explicar melhor no readme, então leia lá por favor...

        // essa linha movimenta o personagem para o lado, depende dos métodos moveRight() e moveLeft(), olhe abaixo para entender melhor...

        // vou explicar a colisão no readme certinho, por enquanto entenda isso:
        // cada afirmação aqui verifica se alguma bloco em volta do personagem está bloqueado
        // se sim (SE ALGUMA DER TRUE) quer dizer que tem um bloco bloqueado, logo o persoangem volta para a posição antiga

        // essa linha movimenta o personagem para o cima

        float oldY = getY();
        translateY(velocity.y * delta);

        // variavel para conferir se colidiu no eixo Y
        grounded = false;

        // se a velocidadeY é menor que 0 está caindo
        if (velocity.y < 0) {
            // segue mesma ideia do eixo X, olhe o readme...
            if (isCellBlocked(getX() + 0.1f, getY()) || isCellBlocked(getX() + getWidth() - 0.1f, getY())) {
                setY((float) Math.floor(getY() + 1f));
                velocity.y = 0;
                grounded = true; // foi com o chão
            }
        } else if (velocity.y > 0) { // pulando, por isso sem grounded = true e verificações diferentes
            if (isCellBlocked(getX() + 0.1f, getY() + getHeight()) || isCellBlocked(getX() + getWidth() - 0.1f, getY() + getHeight())) {
                setY(oldY);
                velocity.y = 0;
            }
        }

        // aqui tenho que ajeitar porque ele ainda não cai. Só cai se pular
        if (getY() <= 0) {
            setY(0);
            velocity.y = 0;
            grounded = true;
        }
    }


    // metodo que verifica se um quadrado está bloqueado, devolve true se sim e false se não
    // recebe tbm x, e y que são as posições do quadrado
    private boolean isCellBlocked(float x, float y) {
        // se não tiver um quadrado de colisão então não tem colisão, returna false
        if (collisionLayer == null) return false;

        // x e y vem em float, então aqui só tem uma conversão para ambas de float -> int
        // por que converter? o tiled precisa de inteiros, só olhar lá que cada quadrado tem uma posição tipo (2, 5)
        int tileX = (int) Math.floor(x);
        int tileY = (int) Math.floor(y);

        // verifica se está dentro do mapa... mais no readme
        if (tileX < 0 || tileX >= collisionLayer.getWidth() || tileY < 0 || tileY >= collisionLayer.getHeight()) {
            return true; // se alguma der verdadeiro ele retorna true, pois tinha um quadrado bloqueado
        }

        //  aqui pegamos o quadrado na posição (x, y), e armazenamos na variavel cell
        // O TiledMapTileLayer.Cell é só o tipo, que é uma "célula"
        TiledMapTileLayer.Cell cell = collisionLayer.getCell(tileX, tileY);

        // aqui a gente descobre se existe um quadrado na célula
        // cell != null: verifica se a célula existe
        // cell.getTile(): verifica se tem um quadrado ali
        // o && faz o seguinte, só retorna true se por acaso os dois forem verdade, existe uma célula e um quadrado
        // se não retorn false e não te nada bloqueando
        return cell != null && cell.getTile() != null;
    }


    // pulo...
    public void jump() {
        // só roda se ele estiver no chão
        if (grounded) {
            velocity.y = jumpStrength;
            grounded = false;
        }
    }

    // coloca velocidade positiva que é ir para direita
    public void moveRight() {
        velocity.x = speed;
    }

    // velocidade negativa para esquerda
    public void moveLeft() {
        velocity.x = -speed;
    }

    // metodo que desenha o personagem na tela, da pra remover, vou ver...
    public void draw(SpriteBatch batch) {
        super.draw(batch);
    }
}
