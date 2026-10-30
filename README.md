Passo a Passo:

1. `git clone https://github.com/luismanfredi/AtividadeJogo.git` (Esse use apenas na primeira vez)

2. `git pull` (Use sempre que começar a codar, pra ter certeza que ta tudo atualizado)

3. Crie um branch com: `git switch -c nomedabranch`

Para o nome da branch, use algo relacionado com oque voce vai fazer. Exemplo: `AdicionarTextura`.

4. Code, e vá adicionando novo código com: `git add arquivo`, e depois `git commit -m "mensagem do que voce fez"`.

No `git add` o nome do arquivo tem que ter o caminho exato, exemplo: `git add src/core/Core.java`. Se quiser conferir os arquivos que precisam ser commitados use `git status` <- muito útil. Não espera pra commitar tudo de uma vez, a cada nova funcionalidade pode fazer commit.

5. Depois: `git push -u origin nomedabranch`, entre no site do github no projeto e clique no botão que vai aparecer chamado "Compare & pull request". Abra o Pull request, Dai você mesmo aprova ou me chama pra ver se ta tudo certo. Depois clique em Merge

7. mude para a branch main com: `git switch main` e por último `git pull`.

6. Por último tbm apague a branch com: `git branch -d nomedabranch` e `git push -d nomedabranch`.

