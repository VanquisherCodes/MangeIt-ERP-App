import json
import os
import joblib
import pandas as pd

model = None

def init():
    global model
    model_path = os.path.join(
        os.getenv("AZUREML_MODEL_DIR", "."),
        "task_cost_linear_regression.joblib"
    )
    model = joblib.load(model_path)

def run(raw_data):
    try:
        data = json.loads(raw_data)

        df = pd.DataFrame([{
            "group_id": data["groupId"],
            "category_name": data["categoryName"],
            "priority": data["priority"],
            "estimated_hours": data["estimatedHours"],
            "team_size": data["teamSize"],
            "allocated_amount": data["allocatedAmount"]
        }])

        prediction = model.predict(df)[0]

        return {
            "predictedCostAmount": round(float(prediction), 2),
            "modelVersion": "v1",
            "modelType": "linear_regression"
        }
    except Exception as e:
        return {"error": str(e)}
