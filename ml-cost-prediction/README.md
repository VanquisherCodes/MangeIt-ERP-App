# ML Cost Prediction

## Run The Notebook

From the project root:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
jupyter notebook notebooks/task_cost_model.ipynb
```

If the browser opens but the notebook kernel is not selected automatically, choose the `Python 3 (ipykernel)` kernel from this project's `.venv`.

## What Was Fixed

- Rebuilt `.venv` to use a consistent Python 3.12 interpreter.
- Verified `notebooks/task_cost_model.ipynb` executes successfully end-to-end.
- Made notebook path resolution robust so it can find `data/` even when launched from a nested working directory.
