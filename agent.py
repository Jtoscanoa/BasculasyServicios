import os
from github import Github
import requests

# Your GitHub Personal Access Token (PAT) - Store this securely!  Ideally use environment variables
github_token = os.environ.get("GITHUB_TOKEN") # Get from environment variable

# LM Studio API Endpoint and Model Path - ADJUST THESE IF DIFFERENT
api_url = "http://127.0.0.1:1234/v1"  # Your LM Studio API endpoint
model_name = "google/gemma-3-12b" # Correct model name from LM Studio

def generate_text(prompt):
    """Generates text using the LM Studio API."""
    headers = {"Content-Type": "application/json"}
    data = {
        "model": model_name,  # Use correct model name
        "messages": [{"role": "user", "content": prompt}], # OpenAI chat completion format
        "max_tokens": 50  # Adjust as needed
    }
    try:
        response = requests.post(api_url + "/chat/completions", headers=headers, json=data)
        response.raise_for_status()  # Raise HTTPError for bad responses (4xx or 5xx)
        return response.json()["choices"][0]["message"]["content"] # Adjust based on LM Studio's API response format
    except requests.exceptions.RequestException as e:
        print(f"Error communicating with LM Studio API: {e}")
        return None

def create_github_pull_request(repo_name, branch_name, commit_message, title, body):
    """Creates a pull request on GitHub."""
    g = Github(github_token)
    repo = g.get_user().get_repo(repo_name)  # Assuming the repo is under your user account
    base_branch = "main" # Or your main branch name

    try:
        new_branch = repo.create_git_ref(ref=f"refs/heads/{branch_name}")
        repo.merge(base_branch, branch_name)
        new_pull_request = repo.create_pull(title=title, body=body, head=branch_name, base=base_branch)
        return f"Pull request created: {new_pull_request.html_url}"
    except Exception as e:
        return f"Error creating pull request: {e}"

# Define a tool for interacting with GitHub
github_tool = Tool(
    name="GitHub Actions",
    func=create_github_pull_request,
    description="Useful for creating pull requests on GitHub. Input should be the repository name, branch name, commit message, title and body of the PR.",
)

# Initialize the agent (using a dummy LLM since we're bypassing it)
dummy_llm = type('DummyLLM', (object,), {'generate': lambda x: "This is a placeholder."})() # Dummy class to avoid errors.
agent = initialize_agent(
    tools=[github_tool],
    llm=dummy_llm,  # Use a dummy LLM since we're bypassing it
    agent=AgentType.ZERO_SHOT_REACT_DESCRIPTION,
    verbose=True  # Set to True for debugging
)

# Run the agent with a task (using our custom text generation function)
task = "Generate a function in Python that calculates the factorial of a number and create a pull request."
result = agent.run(task)
print(result)