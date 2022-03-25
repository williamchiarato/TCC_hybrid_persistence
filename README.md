# hybrid-persistence

## Como configurar os testes de unidade

### Criar no postgres um BD testes com a seguinte tabela
````
create table pessoa (

   id serial,
   nome varchar,
   data_nascimento date,
   primary key (id)

);
````

### Configurar os dados da conexão em `src/test/resources/project.properties`