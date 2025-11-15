# Plano de Desenvolvimento - Desafio de Agendamento de Consultas

## 1. Objetivo
Adaptar o aplicativo base fornecido para criar um Sistema de Gerenciamento de Agendamento de Consultas, utilizando Firebase Auth para autenticação e Firebase Firestore para armazenamento de dados, conforme as Regras de Negócio (RN) e Requisitos Funcionais (RF) do `DESAFIO-A.pdf`.

## 2. Modelo de Dados Firestore (RF05)

Serão utilizadas duas coleções principais: `users` e `appointments`.

### 2.1. Coleção `users`
Armazenará informações adicionais dos usuários, vinculadas ao `uid` do Firebase Auth.

| Campo | Tipo | Descrição |
| :--- | :--- | :--- |
| `uid` | String | ID do usuário do Firebase Auth (Chave Primária) |
| `email` | String | E-mail do usuário |
| `name` | String | Nome completo do usuário |
| `profile` | String | Perfil do usuário: **"Paciente"** ou **"Médico"** (RN02) |

### 2.2. Coleção `appointments`
Armazenará os agendamentos de consultas.

| Campo | Tipo | Descrição |
| :--- | :--- | :--- |
| `patientUid` | String | ID do paciente que agendou a consulta |
| `patientName` | String | Nome do paciente |
| `doctorUid` | String | ID do médico (assumindo que o agendamento é para um médico específico, ou será um campo genérico se não houver seleção de médico) |
| `doctorName` | String | Nome do médico |
| `date` | Timestamp | Data e hora do agendamento (RF02) |
| `status` | String | Status da consulta (e.g., "Agendada", "Cancelada") |

## 3. Modificações no Código Base

O código base em Kotlin/Android já possui a estrutura de autenticação. As modificações serão focadas em:

### 3.1. Cadastro de Usuário (RF01, RN02)
*   **Modificação:** Na `RegisterActivity.kt`, após a criação bem-sucedida do usuário no Firebase Auth, será necessário salvar um documento na coleção `users` do Firestore, incluindo o campo `profile` (Paciente ou Médico).
*   **Ação:** Adicionar um campo de seleção (Spinner ou RadioGroup) na tela de registro para definir o perfil (`Paciente` por padrão, ou uma forma de diferenciar o cadastro de médicos).

### 3.2. Redirecionamento Pós-Login (RN01, RN02)
*   **Modificação:** Na `MainActivity.kt` ou `LoginEmailActivity.kt`, após o login, o aplicativo deve buscar o perfil do usuário na coleção `users` do Firestore.
*   **Lógica:**
    *   Se `profile` for **"Paciente"**: Redirecionar para uma tela de agendamento (`PatientHomeActivity` - nova tela).
    *   Se `profile` for **"Médico"**: Redirecionar para uma tela de visualização de consultas (`DoctorHomeActivity` - nova tela).

### 3.3. Funcionalidades Específicas (RF02, RF03)
*   **Pacientes (RF02):** Criar uma nova Activity (`PatientHomeActivity`) para permitir que o paciente agende uma consulta (seleção de data/hora e médico).
*   **Médicos (RF03):** Adaptar a `UserHomeActivity` existente ou criar uma nova (`DoctorHomeActivity`) para carregar e exibir a lista de consultas agendadas do Firestore.

## 4. Próximos Passos
1.  Configurar o ambiente Firebase (assumindo que o `google-services.json` no código base é um placeholder e o usuário fornecerá o dele, ou eu o criarei).
2.  Criar as classes de modelo de dados (data classes) para `User` e `Appointment`.
3.  Implementar as modificações na `RegisterActivity` para salvar o perfil.
4.  Implementar a lógica de redirecionamento baseada no perfil.
5.  Desenvolver as telas de agendamento e visualização.
