<script>
  import { browser } from '$app/environment';
  import { enhance } from '$app/forms';
  
  let { lessonId } = $props();
  
  let quizState = $state('idle'); // idle | loading | quiz | results
  let questions = $state([]);
  let userAnswers = $state({});
  let score = $state(0);
  let errorMessage = $state('');
  let isGenerating = $state(false);
  
  function handleQuizGenerate() {
    isGenerating = true;
    quizState = 'loading';
    
    return async ({ result, update }) => {
      isGenerating = false;
      
      console.log('Quiz result:', result); // Debug log
      
      if (result.type === 'success' && result.data?.quiz) {
        questions = result.data.quiz;
        userAnswers = {};
        quizState = 'quiz';
      } else if (result.type === 'failure') {
        errorMessage = result.data?.error || 'Failed to generate quiz';
        quizState = 'idle';
      } else {
        console.error('Unexpected result type:', result);
        errorMessage = 'Failed to generate quiz. Please try again.';
        quizState = 'idle';
      }
      
      await update();
    };
  }
  
  function selectAnswer(questionIndex, optionIndex) {
    userAnswers[questionIndex] = optionIndex;
  }
  
  function submitQuiz() {
    let correct = 0;
    questions.forEach((question, index) => {
      if (userAnswers[index] === question.correctAnswer) {
        correct++;
      }
    });
    score = correct;
    quizState = 'results';
  }
  
  function resetQuiz() {
    quizState = 'idle';
    questions = [];
    userAnswers = {};
    score = 0;
    errorMessage = '';
  }
  
  let allAnswered = $derived(
    Object.keys(userAnswers).length === questions.length
  );
</script>

<div class="quiz-container bg-gradient-to-br from-blue-50 to-indigo-50 rounded-2xl p-8 shadow-lg border border-blue-100">
  {#if quizState === 'idle'}
    <div class="text-center">
      <div class="inline-flex items-center justify-center w-20 h-20 bg-gradient-to-br from-blue-500 to-indigo-600 rounded-full mb-6">
        <svg class="w-10 h-10 text-white" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
        </svg>
      </div>
      <h3 class="text-2xl font-bold text-slate-800 mb-3">Test Your Knowledge</h3>
      <p class="text-slate-600 mb-6">Generate a quiz to check your understanding of this lesson</p>
      
      <form method="POST" action="?/generateQuiz" use:enhance={handleQuizGenerate}>
        <button
          type="submit"
          disabled={isGenerating}
          class="px-8 py-4 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl hover:from-blue-700 hover:to-indigo-700 transform hover:scale-105 transition-all duration-200 shadow-md hover:shadow-xl disabled:opacity-50 disabled:cursor-not-allowed disabled:transform-none"
        >
          {isGenerating ? '⏳ Generating...' : '🎯 Generate Quiz'}
        </button>
      </form>
      
      {#if errorMessage}
        <p class="text-red-600 mt-4 text-sm">{errorMessage}</p>
      {/if}
    </div>
  
  {:else if quizState === 'loading'}
    <div class="text-center py-12">
      <div class="inline-block animate-spin rounded-full h-16 w-16 border-4 border-blue-500 border-t-transparent mb-4"></div>
      <p class="text-slate-700 font-medium">✨ Generating your quiz...</p>
      <p class="text-slate-500 text-sm mt-2">This may take a few seconds</p>
    </div>
  
  {:else if quizState === 'quiz'}
    <div>
      <div class="flex justify-between items-center mb-6">
        <h3 class="text-2xl font-bold text-slate-800">Quiz Time! 📝</h3>
        <span class="text-sm text-slate-600">{Object.keys(userAnswers).length}/{questions.length} answered</span>
      </div>
      
      <div class="space-y-6">
        {#each questions as question, qIndex}
          <div class="bg-white rounded-xl p-6 shadow-sm border border-slate-200">
            <p class="font-semibold text-slate-800 mb-4">
              <span class="inline-flex items-center justify-center w-8 h-8 bg-blue-100 text-blue-700 rounded-full text-sm font-bold mr-3">
                {qIndex + 1}
              </span>
              {question.question}
            </p>
            
            <div class="space-y-3">
              {#each question.options as option, oIndex}
                <button
                  type="button"
                  onclick={() => selectAnswer(qIndex, oIndex)}
                  class="w-full text-left p-4 rounded-lg border-2 transition-all duration-200 {
                    userAnswers[qIndex] === oIndex
                      ? 'border-blue-500 bg-blue-50 shadow-sm'
                      : 'border-slate-200 hover:border-slate-300 hover:bg-slate-50'
                  }"
                >
                  <span class="flex items-center gap-3">
                    <span class="flex-shrink-0 w-6 h-6 rounded-full border-2 flex items-center justify-center {
                      userAnswers[qIndex] === oIndex
                        ? 'border-blue-500 bg-blue-500'
                        : 'border-slate-300'
                    }">
                      {#if userAnswers[qIndex] === oIndex}
                        <svg class="w-4 h-4 text-white" fill="currentColor" viewBox="0 0 20 20">
                          <path fill-rule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" clip-rule="evenodd" />
                        </svg>
                      {/if}
                    </span>
                    <span class="text-slate-700">{option}</span>
                  </span>
                </button>
              {/each}
            </div>
          </div>
        {/each}
      </div>
      
      <div class="flex gap-4 mt-8">
        <button
          type="button"
          onclick={submitQuiz}
          disabled={!allAnswered}
          class="flex-1 px-6 py-4 bg-gradient-to-r from-green-600 to-emerald-600 text-white font-semibold rounded-xl hover:from-green-700 hover:to-emerald-700 disabled:from-slate-300 disabled:to-slate-400 disabled:cursor-not-allowed transform hover:scale-105 transition-all duration-200 shadow-md hover:shadow-xl disabled:transform-none"
        >
          {allAnswered ? '✓ Submit Quiz' : `Answer all questions (${Object.keys(userAnswers).length}/${questions.length})`}
        </button>
        
        <button
          type="button"
          onclick={resetQuiz}
          class="px-6 py-4 bg-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-300 transition-colors"
        >
          Cancel
        </button>
      </div>
    </div>
  
  {:else if quizState === 'results'}
    <div class="text-center py-8">
      <div class="inline-flex items-center justify-center w-24 h-24 bg-gradient-to-br from-green-400 to-emerald-500 rounded-full mb-6 shadow-lg">
        <span class="text-4xl">
          {#if score === questions.length}
            🏆
          {:else if score >= questions.length * 0.7}
            🎉
          {:else if score >= questions.length * 0.5}
            👍
          {:else}
            📚
          {/if}
        </span>
      </div>
      
      <h3 class="text-3xl font-bold text-slate-800 mb-2">
        {#if score === questions.length}
          Perfect Score!
        {:else if score >= questions.length * 0.7}
          Great Job!
        {:else if score >= questions.length * 0.5}
          Good Effort!
        {:else}
          Keep Learning!
        {/if}
      </h3>
      
      <p class="text-2xl font-semibold text-slate-700 mb-6">
        You scored <span class="text-blue-600">{score}</span> out of <span class="text-blue-600">{questions.length}</span>
      </p>
      
      <div class="w-full max-w-md mx-auto bg-slate-200 rounded-full h-4 mb-8 overflow-hidden">
        <div 
          class="bg-gradient-to-r from-blue-500 to-indigo-600 h-full rounded-full transition-all duration-1000 ease-out"
          style="width: {(score / questions.length) * 100}%"
        ></div>
      </div>
      
      <!-- Show correct/incorrect answers -->
      <div class="text-left max-w-2xl mx-auto space-y-4 mb-8">
        {#each questions as question, qIndex}
          <div class="bg-white rounded-lg p-4 border-2 {
            userAnswers[qIndex] === question.correctAnswer
              ? 'border-green-300 bg-green-50'
              : 'border-red-300 bg-red-50'
          }">
            <p class="font-medium text-slate-800 mb-2">
              {qIndex + 1}. {question.question}
            </p>
            <p class="text-sm">
              <span class="font-semibold {
                userAnswers[qIndex] === question.correctAnswer
                  ? 'text-green-700'
                  : 'text-red-700'
              }">
                Your answer:
              </span>
              <span class="ml-2 text-slate-700">
                {question.options[userAnswers[qIndex]]}
              </span>
            </p>
            {#if userAnswers[qIndex] !== question.correctAnswer}
              <p class="text-sm mt-1">
                <span class="font-semibold text-green-700">Correct answer:</span>
                <span class="ml-2 text-slate-700">
                  {question.options[question.correctAnswer]}
                </span>
              </p>
            {/if}
          </div>
        {/each}
      </div>
      
      <div class="flex gap-4 justify-center">
        <button
          type="button"
          onclick={resetQuiz}
          class="px-6 py-3 bg-gradient-to-r from-blue-600 to-indigo-600 text-white font-semibold rounded-xl hover:from-blue-700 hover:to-indigo-700 transform hover:scale-105 transition-all duration-200 shadow-md"
        >
          🔄 Try Another Quiz
        </button>
        
        <button
          type="button"
          onclick={resetQuiz}
          class="px-6 py-3 bg-slate-200 text-slate-700 font-semibold rounded-xl hover:bg-slate-300 transition-colors"
        >
          Close
        </button>
      </div>
    </div>
  {/if}
</div>

<style>
  .quiz-container {
    animation: fadeIn 0.3s ease-in;
  }
  
  @keyframes fadeIn {
    from {
      opacity: 0;
      transform: translateY(10px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
</style>