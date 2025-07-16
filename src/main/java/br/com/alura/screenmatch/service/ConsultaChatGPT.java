package br.com.alura.screenmatch.service;

import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

import java.util.List;

public class ConsultaChatGPT {
    public static String obterTraducao(String texto) {
        OpenAiService service = new OpenAiService(System.getenv("OPENAI_APIKEY")); // mantenha sua chave

        // Criando mensagem de usuário
        ChatMessage userMessage = new ChatMessage("user", "Traduza para o português o texto: " + texto);

        // Criando requisição com modelo chat
        ChatCompletionRequest requisicao = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(List.of(userMessage))
                .temperature(0.7)
                .maxTokens(1000)
                .build();

        // Enviando requisição e obtendo resposta
        ChatCompletionResult resultado = service.createChatCompletion(requisicao);

        // Retornando apenas o conteúdo da resposta
        return resultado.getChoices().get(0).getMessage().getContent();
    }
    }




